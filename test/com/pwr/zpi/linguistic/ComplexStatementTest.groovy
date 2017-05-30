import com.pwr.zpi.language.ComplexFormula
import com.pwr.zpi.language.Formula
import com.pwr.zpi.language.LogicOperator
import com.pwr.zpi.language.ModalOperator
import com.pwr.zpi.language.State
import com.pwr.zpi.language.Trait
import com.pwr.zpi.linguistic.ComplexStatement
import com.pwr.zpi.semantic.IndividualModel
import com.pwr.zpi.semantic.ObjectType
import com.pwr.zpi.semantic.QRCode
import org.junit.Test

/**
 * Created by Weronika on 28.05.2017.
 */
class ComplexStatementTest extends GroovyTestCase {

    def objectType
    def model1
    def trait1, trait2, trait3;
    def cf1, cf2, cf3, cf4

    void setUp()
    {
        trait1 = new Trait("Red")
        trait2 = new Trait("Black")
        objectType = new ObjectType("ID", [trait1, trait2, trait3])
        def id = new QRCode("id1")
        model1 = new IndividualModel(id, objectType)
        cf1 = new ComplexFormula(model1, [trait1, trait2], [State.IS, State.IS], LogicOperator.AND)
        cf2 = new ComplexFormula(model1, [trait1, trait2], [State.IS_NOT, State.IS], LogicOperator.AND)
        cf3 = new ComplexFormula(model1, [trait1, trait2], [State.IS, State.IS_NOT], LogicOperator.AND)
        cf4 = new ComplexFormula(model1, [trait1, trait2], [State.IS_NOT, State.IS_NOT], LogicOperator.AND)
    }

    @Test
    void testGenerateStatementKNOW()
    {
        Map<Formula, ModalOperator> map = new HashMap<>();
        map.put(cf1, ModalOperator.KNOW);
        def cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I know that Hyzio is Red and Black")
        map = new HashMap<>();
        map.put(cf2, ModalOperator.KNOW);
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("No, but I know that Hyzio is not Red and Black")
        map = new HashMap<>();
        map.put(cf3, ModalOperator.KNOW);
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("No, but I know that Hyzio is Red and not Black")
        map = new HashMap<>();
        map.put(cf4, ModalOperator.KNOW);
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("No, but I know that Hyzio is not Red and not Black")
        map = new HashMap<>();
        map.put(cf3, ModalOperator.KNOW);
        cs = new ComplexStatement(cf2, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("No, but I know that Hyzio is Red and not Black")
    }

    @Test
    void testGenerateStatementBEL()
    {
        Map<Formula, ModalOperator> map = new HashMap<>();
        map.put(cf1, ModalOperator.BEL);
        def cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I believe that Hyzio is Red and Black")
        map = new HashMap<>()
        map.put(cf2, ModalOperator.BEL)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about those states, however I believe that Hyzio is not Red and Black")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.BEL)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about those states, however I believe that Hyzio is Red and not Black")
        map = new HashMap<>()
        map.put(cf4, ModalOperator.BEL)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about those states, however I believe that Hyzio is not Red and not Black")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I believe that Hyzio is Red and Black, but it is also possible that it is not Red and Black")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about those states, however I believe that Hyzio is Red and not Black, and it is possible that it is not Red and Black")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.BEL)
        map.put(cf1, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I think it is possible that Hyzio is Red and Black, however I believe that it is Red and not Black")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about those states, however I believe that Hyzio is Red and not Black and it is possible that other states are true")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.BEL)
        map.put(cf1, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I think it is possible that Hyzio is Red and Black, however I believe that it is Red and not Black and it is also possible that it is the opposite")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I believe that Hyzio is Red and Black, but it is also possible that it is not Red and Black or even quite the opposite")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        map.put(cf3, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I believe that Hyzio is Red and Black, but it is also possible at given time only one part is true")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.BEL)
        map.put(cf2, ModalOperator.POS)
        map.put(cf3, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("Yes, I believe that Hyzio is Red and Black however all other states are also a little bit possible")
        map = new HashMap<>()
        map.put(cf2, ModalOperator.BEL)
        map.put(cf1, ModalOperator.POS)
        map.put(cf3, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I think it is possible that Hyzio is Red and Black, however I believe that it is not Red and Black and it is even possible that other states are true");
    }

    @Test
    void testGenerateStatementPOS()
    {
        Map<Formula, ModalOperator> map = new HashMap<>();
        map.put(cf1, ModalOperator.POS);
        def cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black")
        map = new HashMap<>()
        map.put(cf2, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about these states, I only think it is possible that it is not Red and Black")
        map = new HashMap<>()
        map.put(cf3, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about these states, I only think it is possible that it is Red and not Black")
        map = new HashMap<>()
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about these states, I only think it is possible that it is not Red and not Black")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black, but it is also possible that it is not Red and not Black")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.POS)
        map.put(cf2, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black, but it is also possible that it is not Red and Black")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.POS)
        map.put(cf2, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black, but it is also possible that it is not Red and Black or even quite the opposite")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.POS)
        map.put(cf2, ModalOperator.POS)
        map.put(cf3, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black, but it is also possible at given time only one part is true")
        map = new HashMap<>()
        map.put(cf1, ModalOperator.POS)
        map.put(cf2, ModalOperator.POS)
        map.put(cf3, ModalOperator.POS)
        map.put(cf4, ModalOperator.POS)
        cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("It is possible that Hyzio is Red and Black, but other options are also possible")
    }

    @Test
    void testGenerateStatementVoid()
    {
        Map<Formula, ModalOperator> map = new HashMap<>()
        def cs = new ComplexStatement(cf1, map, "Hyzio")
        assert cs.generateStatement().equalsIgnoreCase("I do not know what to say about it")
    }




}