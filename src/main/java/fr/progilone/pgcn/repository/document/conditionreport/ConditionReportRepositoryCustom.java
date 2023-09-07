package fr.progilone.pgcn.repository.document.conditionreport;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConditionReportRepositoryCustom {

    /**
     * Recherche paginée de constats d'état
     *
     * @param libraries
     * @param from
     * @param to
     * @param dimensions
     * @param bindings
     * @param docIdentifiers
     * @param pageable
     * @return page d'identifiants
     */
    Page<String> search(List<String> libraries,
                        List<String> projects,
                        List<String> lots,
                        LocalDate from,
                        LocalDate to,
                        DimensionFilter dimensions,
                        Map<String, List<String>> bindings,
                        List<String> docIdentifiers,
                        boolean toValidateOnly,
                        Pageable pageable);

    /**
     * Filtre de recherche sur les dimensions du document
     */
    final class DimensionFilter {

        public enum Operator {
            LTE,
            GTE,
            EQ
        }

        private Operator op = Operator.EQ;
        private int dim1;
        private int dim2;
        private int dim3;

        public DimensionFilter(final Operator op, final Integer dim1, final Integer dim2, final Integer dim3) {
            setOp(op);
            setDim1(dim1);
            setDim2(dim2);
            setDim3(dim3);
        }

        public Operator getOp() {
            return op;
        }

        public void setOp(final Operator op) {
            if (op != null) {
                this.op = op;
            }
        }

        public int getDim1() {
            return dim1;
        }

        public void setDim1(final Integer dim1) {
            if (dim1 != null) {
                this.dim1 = dim1;
            }
        }

        public int getDim2() {
            return dim2;
        }

        public void setDim2(final Integer dim2) {
            if (dim2 != null) {
                this.dim2 = dim2;
            }
        }

        public int getDim3() {
            return dim3;
        }

        public void setDim3(final Integer dim3) {
            if (dim3 != null) {
                this.dim3 = dim3;
            }
        }

        public boolean isEmpty() {
            return dim1 == 0 && dim2 == 0
                   && dim3 == 0;
        }
    }
}
