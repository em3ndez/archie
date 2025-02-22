package com.nedap.archie.rm.composition;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.nedap.archie.rm.archetyped.Archetyped;
import com.nedap.archie.rm.archetyped.FeederAudit;
import com.nedap.archie.rm.archetyped.Link;
import com.nedap.archie.rm.archetyped.Pathable;
import com.nedap.archie.rm.datatypes.CodePhrase;
import com.nedap.archie.rm.datavalues.DvText;
import com.nedap.archie.rm.generic.Participation;
import com.nedap.archie.rm.generic.PartyProxy;
import com.nedap.archie.rm.generic.PartySelf;
import com.nedap.archie.rm.support.identification.ObjectId;
import com.nedap.archie.rm.support.identification.ObjectRef;
import com.nedap.archie.rm.support.identification.UIDBasedId;
import com.nedap.archie.rminfo.Invariant;
import com.nedap.archie.rmutil.InvariantUtil;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by pieter.bos on 04/11/15.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ENTRY", propOrder = {
        "language",
        "encoding",
        "subject",
        "provider",
        "otherParticipations",
        "workflowId"
})
public abstract class Entry extends ContentItem {

    private CodePhrase language;
    private CodePhrase encoding;
    @Nullable

    @XmlElement(name = "workflow_id")
    private ObjectRef<? extends ObjectId> workflowId;
    private PartyProxy subject;
    @Nullable
    private PartyProxy provider;

    @Nullable
    @XmlElement(name = "other_participations")
    private List<Participation> otherParticipations = new ArrayList<>();

    public Entry() {
    }

    public Entry(@Nullable UIDBasedId uid, String archetypeNodeId, DvText name, @Nullable Archetyped archetypeDetails, @Nullable FeederAudit feederAudit, @Nullable List<Link> links, @Nullable Pathable parent, @Nullable String parentAttributeName, CodePhrase language, CodePhrase encoding, PartyProxy subject, @Nullable PartyProxy provider, @Nullable ObjectRef<? extends ObjectId> workflowId, @Nullable List<Participation> otherParticipations) {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent, parentAttributeName);
        setLanguage(language);
        setEncoding(encoding);
        setWorkflowId(workflowId);
        setSubject(subject);
        setProvider(provider);
        setOtherParticipations(otherParticipations);
    }

    public PartyProxy getSubject() {
        return subject;
    }

    public void setSubject(PartyProxy subject) {
        this.subject = subject;
    }

    @Nullable
    public PartyProxy getProvider() {
        return provider;
    }

    public void setProvider(@Nullable PartyProxy provider) {
        this.provider = provider;
    }

    public List<Participation> getOtherParticipations() {
        return otherParticipations;
    }

    public void setOtherParticipations(List<Participation> otherParticipations) {
        this.otherParticipations = otherParticipations;
    }

    public void addOtherParticipant(Participation participant) {
        otherParticipations.add(participant);
    }

    public CodePhrase getLanguage() {
        return language;
    }

    public void setLanguage(CodePhrase language) {
        this.language = language;
    }

    public CodePhrase getEncoding() {
        return encoding;
    }

    public void setEncoding(CodePhrase encoding) {
        this.encoding = encoding;
    }

    @Nullable
    public ObjectRef<? extends ObjectId> getWorkflowId() {
        return workflowId;
    }

    @JsonAlias({"work_flow_id"})
    public void setWorkflowId(@Nullable ObjectRef<? extends ObjectId> workflowId) {
        this.workflowId = workflowId;
    }

    public boolean subjectIsSelf() {
        return subject instanceof PartySelf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Entry entry = (Entry) o;
        return Objects.equals(language, entry.language) &&
                Objects.equals(encoding, entry.encoding) &&
                Objects.equals(workflowId, entry.workflowId) &&
                Objects.equals(subject, entry.subject) &&
                Objects.equals(provider, entry.provider) &&
                Objects.equals(otherParticipations, entry.otherParticipations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), language, encoding, workflowId, subject, provider, otherParticipations);
    }

    @Invariant("Language_valid")
    public boolean languageValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(language, "languages");
    }

    @Invariant("Encoding_valid")
    public boolean encodingValid() {
        return InvariantUtil.belongsToTerminologyByOpenEHRId(encoding, "character sets");
    }

    @Invariant(value="Subject_validity", ignored = true)
    public boolean subjectValid() {
        return true;//this would be _exactly_ the same as the subjectIsSelf method
    }

    @Invariant(value="Other_participations_valid", ignored = true)
    public boolean otherParticipationsValid() {
        return InvariantUtil.nullOrNotEmpty(otherParticipations);
    }

    @Invariant(value="Is_archetypeRoot")
    public boolean archetypeRoot() {
        return isArchetypeRoot();
    }


}
