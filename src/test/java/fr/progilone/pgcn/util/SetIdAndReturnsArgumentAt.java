package fr.progilone.pgcn.util;

import fr.progilone.pgcn.domain.AbstractDomainObject;
import org.mockito.internal.stubbing.answers.ReturnsArgumentAt;
import org.mockito.invocation.InvocationOnMock;

public class SetIdAndReturnsArgumentAt extends ReturnsArgumentAt {

    private final String identifier;

    /**
     * Build the identity answer to return the argument at the given position in the argument array.
     *
     * @param wantedArgumentPosition
     *            The position of the argument identity to return in the invocation.
     *            Using <code>-1</code> indicates the last argument.
     * @param identifier
     */
    public SetIdAndReturnsArgumentAt(final int wantedArgumentPosition, final String identifier) {
        super(wantedArgumentPosition);
        this.identifier = identifier;
    }

    @Override
    public Object answer(final InvocationOnMock invocation) throws Throwable {
        final AbstractDomainObject attachment = (AbstractDomainObject) super.answer(invocation);
        attachment.setIdentifier(identifier);
        return attachment;
    }
}
