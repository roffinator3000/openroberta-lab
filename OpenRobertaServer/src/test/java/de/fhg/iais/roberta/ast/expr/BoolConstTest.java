package de.fhg.iais.roberta.ast.expr;

import org.junit.Assert;
import org.junit.Test;

import de.fhg.iais.roberta.ast.syntax.codeGeneration.Helper;
import de.fhg.iais.roberta.ast.syntax.expr.Assoc;
import de.fhg.iais.roberta.ast.syntax.expr.BoolConst;
import de.fhg.iais.roberta.ast.transformer.JaxbTransformer;

public class BoolConstTest {

    @Test
    public void make() throws Exception {
        String a = "BlockAST [project=[[BoolConst [true]]]]";

        Assert.assertEquals(a, Helper.generateTransformerString("/ast/logic/logic_boolConst.xml"));
    }

    @Test
    public void isValue() throws Exception {
        JaxbTransformer<Void> transformer = Helper.generateTransformer("/ast/logic/logic_boolConst.xml");
        BoolConst<Void> boolConst = (BoolConst<Void>) transformer.getTree().get(0);
        Assert.assertEquals(true, boolConst.isValue());
    }

    @Test
    public void getPresedance() throws Exception {
        JaxbTransformer<Void> transformer = Helper.generateTransformer("/ast/logic/logic_boolConst.xml");
        BoolConst<Void> boolConst = (BoolConst<Void>) transformer.getTree().get(0);
        Assert.assertEquals(999, boolConst.getPrecedence());
    }

    @Test
    public void getAssoc() throws Exception {
        JaxbTransformer<Void> transformer = Helper.generateTransformer("/ast/logic/logic_boolConst.xml");
        BoolConst<Void> boolConst = (BoolConst<Void>) transformer.getTree().get(0);
        Assert.assertEquals(Assoc.NONE, boolConst.getAssoc());
    }
}
