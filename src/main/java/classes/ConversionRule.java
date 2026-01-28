package classes;

public class ConversionRule {

    // combien de PCS pour 1 KG
    private final Double pcsPerKg;

    // combien de L pour 1 KG
    private final Double lPerKg;

    public ConversionRule(Double pcsPerKg, Double lPerKg) {
        this.pcsPerKg = pcsPerKg;
        this.lPerKg = lPerKg;
    }

    public Double getPcsPerKg() {
        if (pcsPerKg == null) {
            throw new IllegalStateException("Conversion PCS <-> KG non supportée");
        }
        return pcsPerKg;
    }

    public Double getLPerKg() {
        if (lPerKg == null) {
            throw new IllegalStateException("Conversion L <-> KG non supportée");
        }
        return lPerKg;
    }
}
