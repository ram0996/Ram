package groovy

import com.affirm.checkout.pojo.AffirmAddressData
import com.affirm.checkout.pojo.AffirmLoanUpdateRequestData
import com.affirm.checkout.pojo.AffirmNameData
import com.affirm.checkout.pojo.AffirmShippingData
import com.affirm.payment.facade.AffirmPaymentFacade

final AffirmPaymentFacade affirmPaymentFacade = spring.getBean("affirmPaymentFacade");


String orderId = "00002001";

def data = new AffirmLoanUpdateRequestData();
data.setOrderId("00002001X");
data.setShippingCarrier("UPS");
data.setShippingConfirmation("ASSEGW22S2S");
data.setShipping(new AffirmShippingData());
data.getShipping().setAddress(new AffirmAddressData());
data.getShipping().getAddress().setLine1("700 8th Ave");
data.getShipping().getAddress().setLine2("Room 221");
data.getShipping().getAddress().setState("New York");
data.getShipping().getAddress().setCity("New York");
data.getShipping().getAddress().setZipcode("10037");
data.getShipping().setName(new AffirmNameData());
data.getShipping().getName().setFirst("Shine")
data.getShipping().getName().setLast("Mathew")

affirmPaymentFacade.updateLoan(orderId, data)