package fr.progilone.pgcn.domain.exchange.template;

public enum Engine {
    /**
     * Template texte, traité avec l'API Velocity
     *
     * @see <a href="http://velocity.apache.org/">Apache Velocity</a>
     */
    Velocity,
    /**
     * Template ODT, traité avec l'API XDocReport
     *
     * @see <a href="https://github.com/opensagres/xdocreport">XDocReport</a>
     */
    XDocReport_ODT
}
