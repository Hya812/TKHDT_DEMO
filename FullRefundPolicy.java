package Metro;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//Strategy Pattern
public class FullRefundPolicy implements RefundPolicy {
	private final int PHUT_DUOC_HOAN = 30;

	private long tinhSoPhut(Ticket ticket) {
		return ChronoUnit.MINUTES.between(ticket.getPurchasedAt(), LocalDateTime.now());
	}

	@Override
	public boolean canRefund(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return false;
		return tinhSoPhut(ticket) <= PHUT_DUOC_HOAN;
	}

	@Override
	public double getRefundAmount(Ticket ticket) {
		if (!canRefund(ticket))
			return 0.0;
		return ticket.getPrice();
	}

	@Override
	public String getRefundReason(Ticket ticket) {
		if (!ticket.getState().canRefund())
			return "Ve da duoc su dung. Khong the hoan";
		if (tinhSoPhut(ticket) <= PHUT_DUOC_HOAN)
			return "Hoan 100%: trong " + PHUT_DUOC_HOAN + " phut.";
		return "Qua " + PHUT_DUOC_HOAN + " phut, khong du dieu kien hoan";
	}

	

}
