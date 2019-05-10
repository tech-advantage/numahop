package fr.progilone.pgcn.domain.dto.help;

import fr.progilone.pgcn.domain.help.HelpPageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleDto {
    private final String name;
    private final HelpPageType type;
    private final List<HelpPageDto> pages = new ArrayList<>();

    public ModuleDto(final String name, final HelpPageType type) {
        super();
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public List<HelpPageDto> getPages() {
        return pages;
    }

    public HelpPageType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass().isInstance(obj)) {
            final ModuleDto other = (ModuleDto) obj;
            return Objects.equals(name, other.name) && Objects.equals(type, other.type);
        }
        return false;
    }

}
