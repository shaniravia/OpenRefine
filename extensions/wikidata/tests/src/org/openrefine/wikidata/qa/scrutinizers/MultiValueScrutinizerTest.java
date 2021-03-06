package org.openrefine.wikidata.qa.scrutinizers;

import org.openrefine.wikidata.qa.ConstraintFetcher;
import org.openrefine.wikidata.testing.TestingData;
import org.openrefine.wikidata.updates.ItemUpdate;
import org.openrefine.wikidata.updates.ItemUpdateBuilder;
import org.testng.annotations.Test;
import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.implementation.StatementImpl;
import org.wikidata.wdtk.datamodel.interfaces.ItemIdValue;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.datamodel.interfaces.Snak;
import org.wikidata.wdtk.datamodel.interfaces.SnakGroup;
import org.wikidata.wdtk.datamodel.interfaces.Statement;
import org.wikidata.wdtk.datamodel.interfaces.Value;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MultiValueScrutinizerTest extends ScrutinizerTest {

    public static PropertyIdValue propertyIdValue = Datamodel.makeWikidataPropertyIdValue("P1963");
    public static Value valueSnak1 = Datamodel.makeWikidataItemIdValue("Q5");
    public static Value valueSnak2 = Datamodel.makeWikidataItemIdValue("Q4");
    public static ItemIdValue entityIdValue = Datamodel.makeWikidataItemIdValue("Q21510857");
    public static PropertyIdValue constraintParameter = Datamodel.makeWikidataPropertyIdValue("P2316");
    public static Value constraintStatus = Datamodel.makeWikidataItemIdValue("Q62026391");

    @Override
    public EditScrutinizer getScrutinizer() {
        return new MultiValueScrutinizer();
    }

    @Test
    public void testNoIssue() {
        ItemIdValue idA = TestingData.existingId;
        ItemIdValue idB = TestingData.matchedId;
        Snak snakValue1 = Datamodel.makeSomeValueSnak(propertyIdValue);
        Snak snakValue2 = Datamodel.makeSomeValueSnak(propertyIdValue);
        Statement statement1 = new StatementImpl("P1963", snakValue1, idA);
        Statement statement2 = new StatementImpl("P1963", snakValue2, idA);
        ItemUpdate update = new ItemUpdateBuilder(idA).addStatement(TestingData.generateStatement(idA, idB))
                .addStatement(TestingData.generateStatement(idA, idB)).addStatement(statement1).addStatement(statement2).build();

        Snak snak = Datamodel.makeValueSnak(constraintParameter, constraintStatus);
        List<Snak> snakList1 = Collections.singletonList(snak);
        SnakGroup snakGroup = Datamodel.makeSnakGroup(snakList1);
        List<SnakGroup> snakGroupList = Collections.singletonList(snakGroup);
        List<Statement> statementList = constraintParameterStatementList(entityIdValue, snakGroupList);
        ConstraintFetcher fetcher = mock(ConstraintFetcher.class);
        when(fetcher.getConstraintsByType(propertyIdValue, "Q21510857")).thenReturn(statementList);
        setFetcher(fetcher);

        scrutinize(update);
        assertNoWarningRaised();
    }

    @Test
    public void testNewItemTrigger() {
        ItemIdValue idA = TestingData.newIdA;
        ItemIdValue idB = TestingData.newIdB;
        Snak mainSnakValue = Datamodel.makeValueSnak(propertyIdValue, valueSnak1);
        Statement statement = new StatementImpl("P1963", mainSnakValue, idA);
        ItemUpdate updateA = new ItemUpdateBuilder(idA).addStatement(TestingData.generateStatement(idA, idB)).addStatement(statement).build();
        ItemUpdate updateB = new ItemUpdateBuilder(idB).addStatement(TestingData.generateStatement(idB, idB)).build();

        Snak snak = Datamodel.makeValueSnak(constraintParameter, constraintStatus);
        List<Snak> snakList1 = Collections.singletonList(snak);
        SnakGroup snakGroup = Datamodel.makeSnakGroup(snakList1);
        List<SnakGroup> snakGroupList = Collections.singletonList(snakGroup);
        List<Statement> statementList = constraintParameterStatementList(entityIdValue, snakGroupList);
        ConstraintFetcher fetcher = mock(ConstraintFetcher.class);
        when(fetcher.getConstraintsByType(propertyIdValue, "Q21510857")).thenReturn(statementList);
        setFetcher(fetcher);

        scrutinize(updateA, updateB);
        assertWarningsRaised(MultiValueScrutinizer.new_type);
    }

    @Test
    public void testExistingItemTrigger() {
        ItemIdValue idA = TestingData.existingId;
        ItemIdValue idB = TestingData.matchedId;
        Snak mainSnakValue = Datamodel.makeValueSnak(propertyIdValue, valueSnak1);
        Statement statement = new StatementImpl("P1963", mainSnakValue, idA);
        ItemUpdate updateA = new ItemUpdateBuilder(idA).addStatement(TestingData.generateStatement(idA, idB)).addStatement(statement).build();
        ItemUpdate updateB = new ItemUpdateBuilder(idB).addStatement(TestingData.generateStatement(idB, idB)).build();

        Snak snak = Datamodel.makeValueSnak(constraintParameter, constraintStatus);
        List<Snak> snakList1 = Collections.singletonList(snak);
        SnakGroup snakGroup = Datamodel.makeSnakGroup(snakList1);
        List<SnakGroup> snakGroupList = Collections.singletonList(snakGroup);
        List<Statement> statementList = constraintParameterStatementList(entityIdValue, snakGroupList);
        ConstraintFetcher fetcher = mock(ConstraintFetcher.class);
        when(fetcher.getConstraintsByType(propertyIdValue, "Q21510857")).thenReturn(statementList);
        setFetcher(fetcher);

        scrutinize(updateA, updateB);
        assertWarningsRaised(MultiValueScrutinizer.existing_type);
    }

}
