package es.stilnovo.library.controller;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.stilnovo.library.model.Product;
import es.stilnovo.library.model.User;
import es.stilnovo.library.model.Inquiry;
import es.stilnovo.library.repository.InquiryRepository;
import es.stilnovo.library.repository.UserRepository;
import es.stilnovo.library.service.MailService;
import es.stilnovo.library.service.ProductService;

@Controller
public class NotificationController {

    @Autowired
    private ProductService productService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InquiryRepository inquiryRepository;


    @PostMapping("/api/v1/notifications/send-inquiry")
    public String sendInquiry(@RequestParam long productId,
                              @RequestParam(required = false) String phone,
                              @RequestParam String type,
                              @RequestParam String message,
                              Principal principal) {
        Product product = productService.findById(productId).orElseThrow();
        if (principal == null) {
            return "redirect:/contact-seller-page/" + productId + "?error=auth";
        }

        User buyer = userRepository.findByName(principal.getName()).orElse(null);
        if (buyer == null) {
            return "redirect:/contact-seller-page/" + productId + "?error=auth";
        }

        Inquiry lastInquiry = inquiryRepository
                .findTopByBuyerIdAndProductIdOrderByCreatedAtDesc(buyer.getUserId(), product.getId());
        if (lastInquiry != null) {
            long secondsSince = Duration.between(lastInquiry.getCreatedAt(), LocalDateTime.now()).getSeconds();
            long cooldown = 1800 - secondsSince;
            if (cooldown > 0) {
                long minutes = (long) Math.ceil(cooldown / 60.0);
                return "redirect:/contact-seller-page/" + productId + "?cooldown=" + minutes;
            }
        }

        String sellerEmail = product.getSeller().getEmail();
        String subject = type + " - " + product.getName();

        String phoneValue = phone == null || phone.isBlank() ? "Not provided" : phone;
        String sellerHtml = MailTemplates.sellerInquiry(product.getId(), product.getName(), type, message);
        String buyerHtml = MailTemplates.buyerConfirmation(product.getName(), type, message);

        Inquiry inquiry = new Inquiry();
        inquiry.setProductId(product.getId());
        inquiry.setProductName(product.getName());
        inquiry.setSellerId(product.getSeller().getUserId());
        inquiry.setSellerEmail(sellerEmail);
        inquiry.setBuyerId(buyer.getUserId());
        inquiry.setBuyerName(buyer.getName());
        inquiry.setBuyerEmail(buyer.getEmail());
        inquiry.setBuyerPhone(phoneValue);
        inquiry.setInquiryType(type);
        inquiry.setMessage(message);
        inquiry.setCreatedAt(LocalDateTime.now());

        try {
            mailService.sendHtml(sellerEmail, subject, sellerHtml);
            mailService.sendHtml(buyer.getEmail(), "We received your message", buyerHtml);
            inquiry.setStatus("SENT");
            inquiryRepository.save(inquiry);
            return "redirect:/contact-seller-page/" + productId + "?sent=true";
        } catch (MailAuthenticationException ex) {
            inquiry.setStatus("FAILED_AUTH");
            inquiryRepository.save(inquiry);
            return "redirect:/contact-seller-page/" + productId + "?error=auth";
        } catch (MailException ex) {
            inquiry.setStatus("FAILED_MAIL");
            inquiryRepository.save(inquiry);
            return "redirect:/contact-seller-page/" + productId + "?error=mail";
        }
    }

    private static class MailTemplates {
        private static String sellerInquiry(long productId, String productName, String type, String message) {
            return "<div style=\"font-family: Arial, sans-serif; color: #1a1f2e;\">"
                    + "<h2 style=\"color:#2f6ced;\">New inquiry received</h2>"
                    + "<p><strong>Product ID:</strong> " + productId + "</p>"
                    + "<p><strong>Product:</strong> " + escape(productName) + "</p>"
                    + "<p><strong>Inquiry type:</strong> " + escape(type) + "</p>"
                    + "<hr style=\"border:none;border-top:1px solid #e6e9f2;margin:16px 0;\"/>"
                    + "<p><strong>Message</strong></p>"
                    + "<p style=\"white-space: pre-line;\">" + escape(message) + "</p>"
                    + "<p style=\"color:#6b7280;font-size:12px;margin-top:24px;\">Sent via Stilnovo</p>"
                    + "</div>";
        }

        private static String buyerConfirmation(String productName, String type, String message) {
            return "<div style=\"font-family: Arial, sans-serif; color: #1a1f2e;\">"
                    + "<h2 style=\"color:#2f6ced;\">We received your message</h2>"
                    + "<p>Your inquiry has been sent to the seller.</p>"
                    + "<p><strong>Product:</strong> " + escape(productName) + "</p>"
                    + "<p><strong>Inquiry type:</strong> " + escape(type) + "</p>"
                    + "<hr style=\"border:none;border-top:1px solid #e6e9f2;margin:16px 0;\"/>"
                    + "<p><strong>Message</strong></p>"
                    + "<p style=\"white-space: pre-line;\">" + escape(message) + "</p>"
                    + "<p style=\"color:#6b7280;font-size:12px;margin-top:24px;\">Stilnovo Support</p>"
                    + "</div>";
        }

        private static String escape(String value) {
            if (value == null) {
                return "";
            }
            return value.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#39;");
        }
    }
}
