package com.nedap.archie.archetypevalidator.validations;

import com.nedap.archie.aom.*;
import com.nedap.archie.archetypevalidator.ErrorType;
import com.nedap.archie.archetypevalidator.ValidatingVisitor;
import com.nedap.archie.flattener.FullArchetypeRepository;
import com.nedap.archie.rules.Assertion;
import org.openehr.utils.message.I18n;

import java.util.List;

public class VariousStructureValidation extends ValidatingVisitor {

    public VariousStructureValidation() {
        super();
    }
    
    protected void beginValidation(Archetype archetype, Archetype flatParent, FullArchetypeRepository repository) {
        this.archetype = archetype;
        this.flatParent = flatParent;
        this.repository = repository;
    }

    @Override
    protected void validate(ArchetypeSlot slot) {

        List<Assertion> includes = slot.getIncludes();
        List<Assertion> excludes = slot.getExcludes();
        if(!includes.isEmpty()) {
            if(includes.get(0).matchesAny()) {
                if(!(excludes.isEmpty() || !excludes.get(0).matchesAny())) {
                    addMessageWithPath(ErrorType.VDSEV, slot.path());
                }
            } else {
                if(!(excludes.isEmpty() || excludes.get(0).matchesAny())) {
                    addMessageWithPath(ErrorType.VDSEV, slot.path());
                }
            }
        }
    }

    @Override
    protected void validate(CComplexObject cComplexObject) {
        if(cComplexObject instanceof CArchetypeRoot) {
            CArchetypeRoot archetypeRoot = (CArchetypeRoot) cComplexObject;
            if(archetypeRoot.getArchetypeRef() != null) {
                if (repository.getArchetype(archetypeRoot.getArchetypeRef()) == null) {
                    addMessageWithPath(ErrorType.VARXRA, cComplexObject.path(),
                            I18n.t("Archetype with id {0} used in use_archetype, but it was not found", archetypeRoot.getArchetypeRef()));
                }

                ArchetypeHRID hrId = new ArchetypeHRID(archetypeRoot.getArchetypeRef());
                String archetypeRootTypeName = cComplexObject.getRmTypeName();
                String archetypeReferenceTypeName = hrId.getRmClass();

                if (metaModel.typeNameExists(archetypeRootTypeName)) {
                    //if parent type info not found will be checked later in phase 2
                    if (!metaModel.typeNameExists(archetypeReferenceTypeName)) {
                        addMessageWithPath(ErrorType.VCORM, cComplexObject.getPath(),
                                I18n.t("Archetype referenced in use_archetype points to class {0}, which does not exist in this reference model", cComplexObject.getRmTypeName()));
                    } else if (!metaModel.rmTypesConformant(archetypeReferenceTypeName, archetypeRootTypeName)) {
                        addMessageWithPath(ErrorType.VARXTV, cComplexObject.getPath(),
                                I18n.t("Use_archetype points to type {0}, which is not conformant for type {1} of the archetype root used",
                                        cComplexObject.getRmTypeName(), archetypeRootTypeName));
                    }
                }
            } else {
                if(!(archetypeRoot.getOccurrences() != null && archetypeRoot.getOccurrences().isProhibited())) {
                    addMessageWithPath(ErrorType.VARXR, archetypeRoot.getPath(),
                            I18n.t("Archetype root must have an archetype reference or be prohibited (occurrences matches 0)"));
                }
            }
        }
    }



}
