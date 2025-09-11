package com.pwdk.grocereach.order.applications.implementation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pwdk.grocereach.Auth.Domain.Entities.User;
import com.pwdk.grocereach.Auth.Infrastructure.Repositories.UserRepository;
import com.pwdk.grocereach.User.Domain.Entities.Address;
import com.pwdk.grocereach.User.Infrastructure.Repositories.AddressRepository;
import com.pwdk.grocereach.inventory.domains.entities.Inventory;
import com.pwdk.grocereach.inventory.infrastructures.repositories.InventoryRepository;
import com.pwdk.grocereach.order.applications.OrderService;
import com.pwdk.grocereach.order.domains.entities.OrderItems;
import com.pwdk.grocereach.order.domains.entities.Orders;
import com.pwdk.grocereach.order.domains.enums.OrderStatus;
import com.pwdk.grocereach.order.infrastructures.repositories.OrderItemsRepository;
import com.pwdk.grocereach.order.infrastructures.repositories.OrdersRepository;
import com.pwdk.grocereach.order.presentations.dtos.CreateOrderRequest;
import com.pwdk.grocereach.order.presentations.dtos.InvoiceResponse;
import com.pwdk.grocereach.order.presentations.dtos.InvoiceResponse.InvoiceResponseItem;
import com.pwdk.grocereach.order.presentations.dtos.OrderItemRequest;
import com.pwdk.grocereach.product.domains.entities.ProductVersions;
import com.pwdk.grocereach.product.infrastructures.repositories.ProductVersionRepository;
import com.pwdk.grocereach.store.infrastructures.repositories.WarehouseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImplementation implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final ProductVersionRepository productVersionRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    @Transactional
    public InvoiceResponse createOrder(String userId, CreateOrderRequest request) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Address address = addressRepository.findById(java.util.UUID.fromString(request.getAddressId()))
            .orElseThrow(() -> new IllegalArgumentException("Address not found"));

        Orders order = Orders.builder()
            .user(user)
            .address(address)
            .status(OrderStatus.PROCESSING)
            .totalPrice(BigDecimal.ZERO)
            .orderedAt(Instant.now())
            .build();

        order = ordersRepository.save(order);

        BigDecimal total = BigDecimal.ZERO;
        List<InvoiceResponseItem> invoiceItems = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            ProductVersions pv = productVersionRepository.findById(itemReq.getProductVersionId())
                .orElseThrow(() -> new IllegalArgumentException("Product version not found"));

            // Create order item using current price
            BigDecimal price = pv.getPrice();
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(itemReq.getQuantity()));

            OrderItems oi = OrderItems.builder()
                .order(order)
                .productVersion(pv)
                .product(pv.getProduct()) // Add this line to set the product
                .quantity(itemReq.getQuantity())
                .price(price)
                .build();
            orderItemsRepository.save(oi);

            // Get warehouse
            var warehouse = warehouseRepository.findById(itemReq.getWarehouseId())
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found"));

            // Fetch latest inventory snapshot from DB for warehouse + product version
            Inventory latestInventory = inventoryRepository.findLatestByWarehouseAndProductVersion(warehouse.getId(), pv.getId());
            if (latestInventory == null) {
                latestInventory = inventoryRepository.findTopByWarehouse_IdAndProductVersion_IdAndDeletedAtIsNullOrderByCreatedAtDesc(warehouse.getId(), pv.getId());
            }
            int currentStock = latestInventory != null && latestInventory.getStock() != null ? latestInventory.getStock() : 0;
            int requestedQty = itemReq.getQuantity();

            if (requestedQty <= 0) {
                throw new IllegalArgumentException("Invalid quantity for product version: " + pv.getId());
            }
            if (currentStock < requestedQty) {
                throw new IllegalArgumentException("Insufficient stock for productVersion=" + pv.getId() + ", warehouse=" + warehouse.getId() + ", currentStock=" + currentStock + ", requested=" + requestedQty);
            }

            int newStock = currentStock - requestedQty;

            // Soft-delete previous active snapshot to keep one active row per pair
            if (latestInventory != null && latestInventory.getDeletedAt() == null) {
                latestInventory.setDeletedAt(Instant.now());
                inventoryRepository.save(latestInventory);
            }

            Inventory inv = Inventory.builder()
                .warehouse(warehouse)
                .productVersion(pv)
                .stock(newStock)
                .journal("-" + requestedQty)
                .build();
            inventoryRepository.save(inv);

            invoiceItems.add(InvoiceResponseItem.builder()
                .productName(pv.getProduct().getName())
                .productVersion("v" + pv.getVersionNumber())
                .warehouseName(inv.getWarehouse().getName())
                .quantity(itemReq.getQuantity())
                .price(price)
                .subTotal(subTotal)
                .build());

            total = total.add(subTotal);
        }

        order.setTotalPrice(total);
        ordersRepository.save(order);

        return InvoiceResponse.builder()
            .orderId(order.getId())
            .userId(order.getUser().getId())
            .addressId(order.getAddress().getId())
            .status(order.getStatus().name())
            .totalPrice(order.getTotalPrice())
            .orderedAt(order.getOrderedAt())
            .items(invoiceItems)
            .build();
    }
}


