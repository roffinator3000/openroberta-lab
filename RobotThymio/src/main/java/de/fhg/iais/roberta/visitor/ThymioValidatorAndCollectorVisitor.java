package de.fhg.iais.roberta.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.WithUserDefinedPort;
import de.fhg.iais.roberta.syntax.lang.blocksequence.ActivityTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.Location;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.StartActivityTask;
import de.fhg.iais.roberta.syntax.lang.expr.ActionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.EvalExpr;
import de.fhg.iais.roberta.syntax.lang.expr.FunctionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.MethodExpr;
import de.fhg.iais.roberta.syntax.lang.expr.SensorExpr;
import de.fhg.iais.roberta.syntax.lang.expr.ShadowExpr;
import de.fhg.iais.roberta.syntax.lang.expr.StmtExpr;
import de.fhg.iais.roberta.syntax.lang.stmt.ActionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.ExprStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.FunctionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.SensorStmt;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.validate.DifferentialMotorValidatorAndCollectorVisitor;


public class ThymioValidatorAndCollectorVisitor extends DifferentialMotorValidatorAndCollectorVisitor {//} implements IMbot2Visitor<Void> {
    
    
    private static final Map<String, String> SENSOR_COMPONENT_TYPE_MAP = new HashMap<String, String>() {{
        put("SOUND_RECORD", SC.SOUND);
//        put("QUAD_COLOR_SENSING", ThymioConstants.MBUILD_QUADRGB);
        put("GYRO_AXIS_RESET", SC.GYRO);
    }};

    public ThymioValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitActionExpr(ActionExpr<Void> actionExpr) {
        return super.visitActionExpr(actionExpr);
    }

    @Override
    public Void visitActionStmt(ActionStmt<Void> actionStmt) {
        return super.visitActionStmt(actionStmt);
    }

    @Override
    public Void visitActivityTask(ActivityTask<Void> activityTask) {
        return super.visitActivityTask(activityTask);
    }

    @Override
    public Void visitEvalExpr(EvalExpr<Void> evalExpr) {
        return super.visitEvalExpr(evalExpr);
    }

    @Override
    public Void visitExprStmt(ExprStmt<Void> exprStmt) {
        return super.visitExprStmt(exprStmt);
    }

    @Override
    public Void visitFunctionExpr(FunctionExpr<Void> functionExpr) {
        return super.visitFunctionExpr(functionExpr);
    }

    @Override
    public Void visitFunctionStmt(FunctionStmt<Void> functionStmt) {
        return super.visitFunctionStmt(functionStmt);
    }

    @Override
    public Void visitLocation(Location<Void> location) {
        return super.visitLocation(location);
    }

    @Override
    public Void visitMainTask(MainTask<Void> mainTask) {
        requiredComponentVisited(mainTask, mainTask.getVariables());
        return null;
    }

    @Override
    public Void visitMethodExpr(MethodExpr<Void> methodExpr) {
        return super.visitMethodExpr(methodExpr);
    }

    @Override
    public Void visitSensorExpr(SensorExpr<Void> sensorExpr) {
        return super.visitSensorExpr(sensorExpr);
    }

    @Override
    public Void visitSensorStmt(SensorStmt<Void> sensorStmt) {
        return super.visitSensorStmt(sensorStmt);
    }

    @Override
    public Void visitShadowExpr(ShadowExpr<Void> shadowExpr) {
        return super.visitShadowExpr(shadowExpr);
    }

    @Override
    public Void visitStartActivityTask(StartActivityTask<Void> startActivityTask) {
        return super.visitStartActivityTask(startActivityTask);
    }

    @Override
    public Void visitStmtExpr(StmtExpr<Void> stmtExpr) {
        return super.visitStmtExpr(stmtExpr);
    }

    @Override
    public Void visitTimerSensor(TimerSensor<Void> timerSensor) {
        return null;
    }

    private void checkActorPort(WithUserDefinedPort<Void> action) {
        Assert.isTrue(action instanceof Phrase, "checking Port of a non Phrase");
        ConfigurationComponent usedConfigurationBlock = this.robotConfiguration.optConfigurationComponent(action.getUserDefinedPort());
        if ( usedConfigurationBlock == null ) {
            Phrase<Void> actionAsPhrase = (Phrase<Void>) action;
            addErrorToPhrase(actionAsPhrase, "CONFIGURATION_ERROR_ACTOR_MISSING");
        }
    }

    private void checkSensorPort(WithUserDefinedPort<Void> sensor) {
        Assert.isTrue(sensor instanceof Phrase, "checking Port of a non Phrase");
        Phrase<Void> sensorAsSensor = (Phrase<Void>) sensor;

        String userDefinedPort = sensor.getUserDefinedPort();
        ConfigurationComponent configurationComponent = this.robotConfiguration.optConfigurationComponent(userDefinedPort);
        if ( configurationComponent == null ) {
            configurationComponent = getSubComponent(userDefinedPort);
            if ( configurationComponent == null ) {
                addErrorToPhrase(sensorAsSensor, "CONFIGURATION_ERROR_SENSOR_MISSING");
                return;
            }
        }
        checkSensorType(sensorAsSensor, configurationComponent);
    }

    private void checkSensorType(Phrase<Void> sensor, ConfigurationComponent configurationComponent) {
        String expectedComponentType = SENSOR_COMPONENT_TYPE_MAP.get(sensor.getKind().getName());
        String typeWithoutSensing = sensor.getKind().getName().replace("_SENSING", "");
        if ( !(typeWithoutSensing.equalsIgnoreCase(configurationComponent.getComponentType())) ) {
            if ( expectedComponentType != null && !expectedComponentType.equalsIgnoreCase(configurationComponent.getComponentType()) ) {
                addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
            }
        }
    }

    private ConfigurationComponent getSubComponent(String userDefinedPort) {
        for ( ConfigurationComponent component : this.robotConfiguration.getConfigurationComponentsValues() ) {
            try {
                for ( List<ConfigurationComponent> subComponents : component.getSubComponents().values() ) {
                    for ( ConfigurationComponent subComponent : subComponents ) {
                        if ( subComponent.getUserDefinedPortName().equals(userDefinedPort) ) {
                            return subComponent;
                        }
                    }
                }
            } catch ( UnsupportedOperationException e ) {
                continue;
            }
        }
        return null;
    }

}
