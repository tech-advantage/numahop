package fr.progilone.pgcn.domain.exchange.template;

public enum Name {

    /**
     * Constat d'état
     */
    ConditionReport(Engine.Velocity),
    /**
     * Mail de réinitialisation de mot de passe
     */
    ReinitPassword(Engine.XDocReport_ODT),
    /**
     * Mail de création d'un utilisateur
     */
    UserCreation(Engine.Velocity),
    /**
     * Bordereau de constat d'état
     */
    ConditionReportSlip(Engine.Velocity),
    /**
     * Bordereau de livraison
     */
    DeliverySlip(Engine.Velocity),
    /**
     * Bordereau de contrôle
     */
    ControlSlip(Engine.Velocity);

    /**
     * Moteur de template utilisé
     */
    private final Engine engine;

    Name(final Engine defaultEngine) {
        this.engine = defaultEngine;
    }

    public Engine getEngine() {
        return engine;
    }
}
