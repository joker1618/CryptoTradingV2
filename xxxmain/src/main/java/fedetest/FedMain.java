package fedetest;

import com.fede.ct.v2.common.context.CryptoContext;
import com.fede.ct.v2.common.context.RunType;
import com.fede.ct.v2.common.model._private.OrderInfo;
import com.fede.ct.v2.datalayer.IModelTrading;
import com.fede.ct.v2.datalayer.impl.ModelFactory;
import com.fede.ct.v2.login.LoginService;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Arrays;
import java.util.List;

import static java.lang.System.out;

/**
 * Created by f.barbano on 02/12/2017.
 */
public class FedMain {

	public static void main(String[] args) {

		CryptoContext ctx = LoginService.createContext(RunType.SIMPLE_THRESOLD, 1);
		IModelTrading modelTrading = ModelFactory.createModelTrading(ctx);

//		List<OrderInfo> ordersStatus = modelTrading.getOrdersStatus(Arrays.asList("O2IVW3-4ER3L-CROK2J"));
		List<OrderInfo> ordersStatus = modelTrading.getOrdersStatus(Arrays.asList("O2IVW3-4ER3L-CROK2J", "O3CSE3-F6LJY-36HUWS"));

		ordersStatus.forEach(orderInfo -> {
			out.println(ToStringBuilder.reflectionToString(orderInfo, ToStringStyle.MULTI_LINE_STYLE));
			out.println();
		});
	}
}
