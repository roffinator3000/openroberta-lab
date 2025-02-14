package de.fhg.iais.roberta.syntax.lang.stmt;

import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.NepoField;
import de.fhg.iais.roberta.transformer.NepoPhrase;
import de.fhg.iais.roberta.transformer.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;

@NepoPhrase(containerType = "NN_CHANGE_BIAS_STMT")
public class NNChangeBiasStmt<V> extends Stmt<V> {
    @NepoField(name = BlocklyConstants.NAME)
    public final String name;
    @NepoField(name = BlocklyConstants.CHANGE)
    public final String change;
    @NepoValue(name = BlocklyConstants.VALUE, type = BlocklyType.NUMBER)
    public final Expr<V> value;

    public NNChangeBiasStmt(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, String name, String change, Expr<V> value) {
        super(kind, properties, comment);
        Assert.isTrue(value.isReadOnly() && value != null);
        this.name = name;
        this.change = change;
        this.value = value;
        setReadOnly();
    }

    public static <V> NNChangeBiasStmt<V> make(BlocklyBlockProperties properties, BlocklyComment comment, String name, String change, Expr<V> value) {
        return new NNChangeBiasStmt<V>(BlockTypeContainer.getByName("NN_INPUT_NEURON_STMT"), properties, comment, name, change, value);
    }

    public String getName() {
        return this.name;
    }

    public String getChange() {
        return this.change;
    }

    public Expr<V> getValue() {
        return this.value;
    }
}
