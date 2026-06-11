package main;

import java.time.LocalDate;
import Metro.*;
import  view.*;
import controller.*;
/**
 * Demo chạy thử StaffView + StaffController
 * Tạo sẵn dữ liệu mẫu để test các tính năng
 */
public class StaffMain {

    public static void main(String[] args) {

        // ── 1. Tạo dữ liệu mẫu ──────────────────────────────

        // Tuyến & ga
        MetroLine line = new MetroLine("L1", "Ben Thanh - Suoi Tien");
        Station   s1   = new Station("S01", "Ben Thanh", line, 100);
        Station   s2   = new Station("S02", "Ba Son",    line, 80);
        line.addStation(s1);
        line.addStation(s2);

        // Hành khách
        Passenger p1 = new Passenger("P001", "Nguyen Van A", PassengerType.NORMAL,  "ID001", 200000);
        Passenger p2 = new Passenger("P002", "Tran Thi B",   PassengerType.STUDENT, "ID002", 100000);

        // Tạo vé vào TicketManager
        TicketManager tm = TicketManager.getInstance();

        Ticket tActive = tm.issueTicket(p1, TicketType.SINGLE, 3);
        Ticket tUsed = tm.issueTicket(p2, TicketType.DAILY, 0);
        Ticket tExpired = tm.issueTicket(p1, TicketType.MONTHLY, 0);

        tActive.setStrategy(new FullRefundPolicy());
        tUsed.setStrategy(new FullRefundPolicy());
        tExpired.setStrategy(new FullRefundPolicy());

      

        // Giả lập trạng thái
        tUsed.checkIn();       // ACTIVE → USED
        tExpired.checkIn();    // ACTIVE → USED
        tExpired.checkOut();   // USED   → EXPIRED

        System.out.println("=== VÉ MẪU ===");
        System.out.println("ACTIVE  ticket ID: " + tActive.getTicketId());
        System.out.println("USED    ticket ID: " + tUsed.getTicketId());
        System.out.println("EXPIRED ticket ID: " + tExpired.getTicketId());
        System.out.println("==============");

        // ── 2. Đăng ký CitizenInfo cho VerifyService ─────────
        VerifyService vs = VerifyService.getInstance();
        vs.registerCitizen(new CitizenInfo("ID002", "Tran Thi B",
                LocalDate.of(2004, 1, 1), true, false));

        // ── 3. Tạo nhân viên ─────────────────────────────────
        StationStaff staff = new StationStaff("ST001", "Nguyen Thi Staff", "pass123", "ST001");

        // ── 4. Khởi tạo MVC ──────────────────────────────────
        StaffView       view       = new StaffView();
        StaffController controller = new StaffController(staff, view);
        view.setController(controller);
        HeatmapService.getInstance().attach(view);

        // ── 5. Giả lập HeatMap alert sau 3 giây ─────────────
        new Thread(() -> {
            try {
                Thread.sleep(3000);
                // Check-in nhiều người để kích hoạt alert
                for (int i = 0; i < 55; i++) s1.incrementCheckIn();
                HeatmapService.getInstance().analyzeRealtime(s1); // ATTENTION
                Thread.sleep(2000);
                for (int i = 0; i < 30; i++) s1.incrementCheckIn();
                HeatmapService.getInstance().analyzeRealtime(s1); // WARNING/CRITICAL
            } catch (InterruptedException ignored) {}
        }).start();

        // ── 6. Hiển thị UI ───────────────────────────────────
        view.show();
    }
}