package fr.progilone.pgcn.service.document.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.progilone.pgcn.domain.document.Check;
import fr.progilone.pgcn.domain.dto.document.CheckDTO;
import fr.progilone.pgcn.service.document.DocPageService;

@Service
public class UICheckMapper {

    @Autowired
	private DocPageService docPageService;
	
	public void mapInto(final CheckDTO checkDTO, final Check check) {
		check.setErrorLabel(Check.ErrorLabel.valueOf(checkDTO.getErrorLabel()));
		check.setErrorType(Check.ErrorType.valueOf(checkDTO.getErrorType()));
		check.setPage(docPageService.findOne(checkDTO.getPage().getIdentifier()));
    }
}
