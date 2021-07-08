package fr.progilone.pgcn.util;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.invocation.InvocationOnMock;

/**
 * Étend ReturnsArgumentAt, ajoute un identifiant à l'argument et le met de côté
 *
 * @see ReturnsArgumentAt
 */
public class CatchAndReturnArgumentAt<T extends AbstractDomainObject> extends ReturnsArgumentAt {

    private final String identifier;
    private T domainObject;

    /**
     * Build the identity answer to return the argument at the given position in the argument array.
     *
     * @param wantedArgumentPosition
     *         The position of the argument identity to return in the invocation.
     *         Using <code>-1</code> indicates the last argument.
     * @param identifier
     */
    public CatchAndReturnArgumentAt(final int wantedArgumentPosition, final String identifier) {
        super(wantedArgumentPosition);
        this.identifier = identifier;
    }

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
        final T answer = (T) super.answer(invocation);
        answer.setIdentifier(identifier);

        domainObject = answer;
        return answer;
    }

    public T getDomainObject() {
        return domainObject;
    }
}
