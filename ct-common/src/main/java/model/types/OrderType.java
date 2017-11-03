package model.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.barbano on 13/09/2017.
 */
public enum OrderType {
//	market
	MARKET("market", "m"),
//	limit (price = limit price)
	LIMIT("limit", "l"),
//	stop-loss (price = stop loss price)
	STOP_LOSS("stop-loss"),
//	take-profit (price = take profit price)
	TAKE_PROFIT("take-profit"),
//	stop-loss-profit (price = stop loss price, price2 = take profit price)
	STOP_LOSS_PROFIT("stop-loss-profit"),
//	stop-loss-profit-limit (price = stop loss price, price2 = take profit price)
	STOP_LOSS_PROFIT_LIMIT("stop-loss-profit-limit"),
//	stop-loss-limit (price = stop loss trigger price, price2 = triggered limit price)
	STOP_LOSS_LIMIT("stop-loss-limit"),
//	take-profit-limit (price = take profit trigger price, price2 = triggered limit price)
	TAKE_PROFIT_LIMIT("take-profit-limit"),
//	trailing-stop (price = trailing stop offset)
	TRAILING_STOP("trailing-stop"),
//	trailing-stop-limit (price = trailing stop offset, price2 = triggered limit offset)
	TRAILING_STOP_LIMIT("trailing-stop-limit"),
//	stop-loss-and-limit (price = stop loss price, price2 = limit price)
	STOP_LOSS_AND_LIMIT("stop-loss-and-limit"),
//	settle-position
	SETTLE_POSITION("settle-position")
	;

	private List<String> labels;

	OrderType(String main, String... others) {
		this.labels = new ArrayList<>();
		labels.add(main);
		labels.addAll(Arrays.asList(others));
	}

	public String label() {
		return labels.get(0);
	}

	public static OrderType getByLabel(String toFind) {
		return Arrays.stream(values())
				   .filter(at -> at.labels.contains(toFind))
				   .findAny()
				   .orElse(null);
	}
}
