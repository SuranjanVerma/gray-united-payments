package com.grayunited.payments.service;

import com.grayunited.payments.entity.Course;
import com.grayunited.payments.repository.CourseRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final CourseRepository courseRepository;

    @Value("${razorpay.api.key}")
    private String razorpayKeyId;

    @Value("${razorpay.api.secret}")
    private String razorpaySecret;

    private RazorpayClient razorpayClient;

    // Inject the Repository to fetch live prices dynamically
    public CheckoutService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PostConstruct
    public void init() {
        try {
            this.razorpayClient = new RazorpayClient(razorpayKeyId, razorpaySecret);
            log.info("Razorpay Client securely initialized.");
        } catch (Exception e) {
            log.error("Failed to initialize Razorpay Client. Check your API keys.", e);
        }
    }

    /**
     * Generates a Razorpay order securely using live database pricing.
     */
    public Map<String, Object> createOrder(String courseId, String enrollmentId, String userId) throws Exception {

        // 1. Fetch the exact course price from the database
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found in database: " + courseId));

        Long amountInPaise = course.getPriceInPaise();

        // 2. Build the Razorpay Order payload
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amountInPaise);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", enrollmentId);

        // Attach structural metadata for tracking
        JSONObject notes = new JSONObject();
        notes.put("course_id", courseId);
        notes.put("user_id", userId);
        orderRequest.put("notes", notes);

        // 3. Contact Razorpay servers to create the order
        Order order = razorpayClient.orders.create(orderRequest);

        // 4. Return the order ID and the dynamic amount to the Controller
        return Map.of(
                "order_id", order.get("id").toString(),
                "amount", amountInPaise
        );
    }

    /**
     * Mathematically verifies that the payment success message from the frontend is genuine.
     */
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, razorpaySecret);
        } catch (Exception e) {
            log.error("Signature verification exception occurred", e);
            return false;
        }
    }
}