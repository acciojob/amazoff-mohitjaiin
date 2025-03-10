package com.driver;

import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class OrderRepository {

    private HashMap<String, Order> orderMap;
    private HashMap<String, DeliveryPartner> partnerMap;
    private HashMap<String, HashSet<String>> partnerToOrderMap;
    private HashMap<String, String> orderToPartnerMap;

    public OrderRepository() {
        this.orderMap = new HashMap<>();
        this.partnerMap = new HashMap<>();
        this.partnerToOrderMap = new HashMap<>();
        this.orderToPartnerMap = new HashMap<>();
    }

    public void saveOrder(Order order) {
        orderMap.put(order.getId(), order);
    }

    public void savePartner(String partnerId) {
        partnerMap.put(partnerId, new DeliveryPartner(partnerId));
    }

    public void saveOrderPartnerMap(String orderId, String partnerId) {
        if (orderMap.containsKey(orderId) && partnerMap.containsKey(partnerId)) {
            partnerToOrderMap.putIfAbsent(partnerId, new HashSet<>());
            partnerToOrderMap.get(partnerId).add(orderId);
            orderToPartnerMap.put(orderId, partnerId);

            DeliveryPartner partner = partnerMap.get(partnerId);
            partner.setNumberOfOrders(partner.getNumberOfOrders() + 1);
        }
    }

    public Order findOrderById(String orderId) {
        return orderMap.getOrDefault(orderId, null);
    }

    public DeliveryPartner findPartnerById(String partnerId) {
        return partnerMap.getOrDefault(partnerId, null);
    }

    public Integer findOrderCountByPartnerId(String partnerId) {
        return partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()).size();
    }

    public List<String> findOrdersByPartnerId(String partnerId) {
        return new ArrayList<>(partnerToOrderMap.getOrDefault(partnerId, new HashSet<>()));
    }

    public List<String> findAllOrders() {
        return new ArrayList<>(orderMap.keySet());
    }

    public void deletePartner(String partnerId) {
        if (partnerMap.containsKey(partnerId)) {
            for (String orderId : partnerToOrderMap.getOrDefault(partnerId, new HashSet<>())) {
                orderToPartnerMap.remove(orderId);
            }
            partnerToOrderMap.remove(partnerId);
            partnerMap.remove(partnerId);
        }
    }

    public void deleteOrder(String orderId) {
        if (orderMap.containsKey(orderId)) {
            String partnerId = orderToPartnerMap.get(orderId);
            if (partnerId != null) {
                partnerToOrderMap.get(partnerId).remove(orderId);
                orderToPartnerMap.remove(orderId);
                DeliveryPartner partner = partnerMap.get(partnerId);
                partner.setNumberOfOrders(partner.getNumberOfOrders() - 1);
            }
            orderMap.remove(orderId);
        }
    }

    public Integer findCountOfUnassignedOrders() {
        return orderMap.size() - orderToPartnerMap.size();
    }

    public Integer findOrdersLeftAfterGivenTimeByPartnerId(String timeString, String partnerId) {
        if (!partnerToOrderMap.containsKey(partnerId)) return 0;

        String[] timeParts = timeString.split(":");
        int givenTime = Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);

        int count = 0;
        for (String orderId : partnerToOrderMap.get(partnerId)) {
            if (orderMap.get(orderId).getDeliveryTime() > givenTime) {
                count++;
            }
        }
        return count;
    }

    public String findLastDeliveryTimeByPartnerId(String partnerId) {
        if (!partnerToOrderMap.containsKey(partnerId) || partnerToOrderMap.get(partnerId).isEmpty()) {
            return "00:00";
        }

        int lastTime = 0;
        for (String orderId : partnerToOrderMap.get(partnerId)) {
            lastTime = Math.max(lastTime, orderMap.get(orderId).getDeliveryTime());
        }

        return String.format("%02d:%02d", lastTime / 60, lastTime % 60);
    }
}
