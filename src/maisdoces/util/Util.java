package maisdoces.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Metodos de ajuda para formatar valores e datas.
 */
public class Util {

    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale BRASIL = Locale.forLanguageTag("pt-BR");

    public static String moeda(double valor) {
        return String.format(BRASIL, "R$ %,.2f", valor);
    }

    public static String data(LocalDate data) {
        if (data == null) {
            return "";
        }
        return data.format(FORMATO_DATA);
    }

    // retorna null se o campo estiver vazio
    public static LocalDate lerData(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(texto.trim(), FORMATO_DATA);
    }

    // aceita 3,50 ou 3.50
    public static double lerValor(String texto) {
        String t = texto.trim().replace("R$", "").replace(" ", "");
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");
        }
        return Double.parseDouble(t);
    }
}
