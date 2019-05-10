package fr.progilone.pgcn.domain.dto.help;

import com.mysema.query.annotations.QueryProjection;
import fr.progilone.pgcn.domain.help.HelpPageType;

import java.util.ArrayList;
import java.util.List;

public class HelpPageDto {

    private final String identifier;
    private final String title;
    private final int rank;
    private final String module;
    private final HelpPageType type;
    private final String parent;
    private final List<HelpPageDto> children = new ArrayList<>();

    @QueryProjection
    public HelpPageDto(final String identifier, final String title, final int rank, final String module, final HelpPageType type, final String parent) {
        super();
        this.identifier = identifier;
        this.title = title;
        this.rank = rank;
        this.module = module;
        this.type = type;
        this.parent = parent;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getTitle() {
        return title;
    }

    public int getRank() {
        return rank;
    }

    public String getModule() {
        return module;
    }

    public HelpPageType getType() {
        return type;
    }

    public String getParent() {
        return parent;
    }

    public List<HelpPageDto> getChildren() {
        return children;
    }

}
