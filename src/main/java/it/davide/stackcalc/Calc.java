package it.davide.stackcalc;

/**
 * Parser recursive descent minimale.
 * Supporta: + - * / % ^ parentesi, unario negativo e suffissi tipo 3s / 2h / 5k.
 */
public final class Calc {

	private final String s;
	private int i;

	private Calc(String input) {
		this.s = input.replace(" ", "").replace("_", "").replace(",", "");
	}

	public static double eval(String input) {
		Calc c = new Calc(input);
		double v = c.expr();
		if (c.i < c.s.length()) {
			throw new CalcException("stackcalc.error.unexpected_char", c.s.charAt(c.i));
		}
		if (Double.isNaN(v) || Double.isInfinite(v)) {
			throw new CalcException("stackcalc.error.invalid_result");
		}
		return v;
	}

	// expr := term (('+' | '-') term)*
	private double expr() {
		double v = term();
		while (i < s.length()) {
			char c = s.charAt(i);
			if (c == '+') {
				i++;
				v += term();
			} else if (c == '-') {
				i++;
				v -= term();
			} else {
				break;
			}
		}
		return v;
	}

	// term := power (('*' | 'x' | '/' | '%') power)*
	private double term() {
		double v = power();
		while (i < s.length()) {
			char c = s.charAt(i);
			if (c == '*' || c == 'x' || c == 'X') {
				i++;
				v *= power();
			} else if (c == '/') {
				i++;
				double d = power();
				if (d == 0) throw new CalcException("stackcalc.error.div_zero");
				v /= d;
			} else if (c == '%') {
				i++;
				double d = power();
				if (d == 0) throw new CalcException("stackcalc.error.mod_zero");
				v %= d;
			} else if (c == '(') {
				// moltiplicazione implicita: 2(3+4) = 14
				v *= power();
			} else {
				break;
			}
		}
		return v;
	}

	// power := unary ('^' power)?   -> associativo a destra
	private double power() {
		double v = unary();
		if (i < s.length() && s.charAt(i) == '^') {
			i++;
			v = Math.pow(v, power());
		}
		return v;
	}

	private double unary() {
		if (i < s.length() && s.charAt(i) == '-') {
			i++;
			return -unary();
		}
		if (i < s.length() && s.charAt(i) == '+') {
			i++;
			return unary();
		}
		if (i < s.length() && s.charAt(i) == '(') {
			i++;
			double v = expr();
			if (i >= s.length() || s.charAt(i) != ')') {
				throw new CalcException("stackcalc.error.missing_paren");
			}
			i++;
			return v * suffix();
		}
		return number();
	}

	private double number() {
		int start = i;
		while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '.')) {
			i++;
		}
		if (start == i) {
			throw new CalcException("stackcalc.error.expected_number");
		}
		double v;
		try {
			v = Double.parseDouble(s.substring(start, i));
		} catch (NumberFormatException e) {
			throw new CalcException("stackcalc.error.malformed_number");
		}
		return v * suffix();
	}

	/**
	 * Legge le lettere subito dopo un numero e le interpreta come moltiplicatore.
	 * Se non riconosce la sequenza torna indietro, cosi' la 'x' resta usabile
	 * come segno di moltiplicazione.
	 */
	private double suffix() {
		int start = i;
		while (i < s.length() && Character.isLetter(s.charAt(i))) {
			i++;
		}
		if (start == i) return 1;

		String t = s.substring(start, i).toLowerCase();
		double m = switch (t) {
			case "s", "st", "stack", "stacks" -> 64;
			case "h", "sh", "shulker", "c", "chest" -> 1728;
			case "dc" -> 3456;
			case "ec", "ender" -> 1728;
			case "k" -> 1_000;
			case "mi", "mil" -> 1_000_000;
			default -> -1;
		};

		if (m < 0) {
			i = start; // non era un suffisso, restituisci i caratteri al parser
			return 1;
		}
		return m;
	}
}
