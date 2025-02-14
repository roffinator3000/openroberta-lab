package de.fhg.iais.roberta.visitor.validate;

import java.util.Optional;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;

public class ArduinoDifferentialMotorValidatorAndCollectorVisitor extends ArduinoValidatorAndCollectorVisitor{
    public ArduinoDifferentialMotorValidatorAndCollectorVisitor(ConfigurationAst brickConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(brickConfiguration, beanBuilders);
    }

    public Void visitDriveAction(DriveAction<Void> driveAction) {
        checkAndVisitMotionParam(driveAction, driveAction.getParam());
        addMotorsToUsedActors();
        return null;
    }

    public Void visitCurveAction(CurveAction<Void> curveAction) {
        requiredComponentVisited(curveAction, curveAction.getParamLeft().getSpeed(), curveAction.getParamRight().getSpeed());
        Optional.ofNullable(curveAction.getParamLeft().getDuration()).ifPresent(duration -> requiredComponentVisited(curveAction, duration.getValue()));
        Optional.ofNullable(curveAction.getParamRight().getDuration()).ifPresent(duration -> requiredComponentVisited(curveAction, duration.getValue()));
        checkForZeroSpeedInCurve(curveAction.getParamLeft().getSpeed(), curveAction.getParamRight().getSpeed(), curveAction);
        addMotorsToUsedActors();
        return null;
    }

    public Void visitMotorDriveStopAction(MotorDriveStopAction<Void> stopAction) {
        addMotorsToUsedActors();
        return null;
    }

    public Void visitTurnAction(TurnAction<Void> turnAction) {
        checkAndVisitMotionParam(turnAction, turnAction.getParam());
        addMotorsToUsedActors();
        return null;
    }

    protected void checkForZeroSpeedInCurve(Expr<Void> speedLeft, Expr<Void> speedRight, Action<Void> action) {
        if ( speedLeft.getKind().hasName("NUM_CONST") && speedRight.getKind().hasName("NUM_CONST") ) {
            double speedLeftNumConst = Double.parseDouble(((NumConst<Void>) speedLeft).getValue());
            double speedRightNumConst = Double.parseDouble(((NumConst<Void>) speedRight).getValue());
            boolean bothMotorsHaveZeroSpeed = (Math.abs(speedLeftNumConst) < DOUBLE_EPS) && (Math.abs(speedRightNumConst) < DOUBLE_EPS);
            if ( bothMotorsHaveZeroSpeed ) {
                addWarningToPhrase(action, "MOTOR_SPEED_0");
            }
        }
    }

    protected void addMotorsToUsedActors() {
        usedHardwareBuilder.addUsedActor(new UsedActor("B", SC.MEDIUM));
        usedHardwareBuilder.addUsedActor(new UsedActor("A", SC.MEDIUM));
    }

    protected void checkLeftRightMotorPort(Phrase<Void> driveAction) {
        ConfigurationComponent leftMotor = this.robotConfiguration.getFirstMotor(SC.LEFT);
        ConfigurationComponent rightMotor = this.robotConfiguration.getFirstMotor(SC.RIGHT);
        checkRightMotorPresenceAndLeftMotorPresence(driveAction, rightMotor, leftMotor);
    }

    protected void checkRightMotorPresenceAndLeftMotorPresence(Phrase<Void> driveAction, ConfigurationComponent rightMotor, ConfigurationComponent leftMotor) {
        if ( rightMotor == null ) {
            addErrorToPhrase(driveAction, "CONFIGURATION_ERROR_MOTOR_RIGHT_MISSING");
        }
        if ( leftMotor == null ) {
            addErrorToPhrase(driveAction, "CONFIGURATION_ERROR_MOTOR_LEFT_MISSING");
        }
    }

}
