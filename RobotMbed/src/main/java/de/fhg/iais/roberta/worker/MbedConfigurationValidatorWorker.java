package de.fhg.iais.roberta.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.typecheck.NepoInfo;
import de.fhg.iais.roberta.util.Key;
import de.fhg.iais.roberta.util.dbc.DbcException;

public class MbedConfigurationValidatorWorker {
    private static final String PORT = "PIN1";
    private static final Set<String> CALLIBOT_PINS = Stream.of("1", "2", "4", "5", "C16", "C17").collect(Collectors.toCollection(HashSet::new));
    private final Project project;

    public MbedConfigurationValidatorWorker(Project project) {
        this.project = project;
    }

    public void validateConfiguration(
        List<String> freePins,
        List<String> defaultProps,
        List<String> existPins,
        HashMap<String, String> mapCorrectConfigPins) {

        List<String> currentFreePins = new ArrayList<>(freePins);
        List<String> defaultProperties = new ArrayList<>(defaultProps);
        List<String> existingPins = new ArrayList<String>(existPins) {{
            add("A0");
        }};
        project.getConfigurationAst().getConfigurationComponents().forEach((k, configurationComponent) -> {
            if ( !defaultProperties.contains(configurationComponent.getComponentType()) ) {
                checkPinOverlap(configurationComponent, currentFreePins, existingPins, mapCorrectConfigPins);
            }
        });
    }

    public void checkPinOverlap(
        ConfigurationComponent configurationComponent,
        List<String> currentFreePins,
        List<String> existingPins,
        HashMap<String, String> correctConfigPins) {

        Map<String, String> componentProperties = configurationComponent.getComponentProperties();
        List<String> blockPins = new ArrayList<>();
        String componentType = configurationComponent.getComponentType();
        if ( componentType.equals(SC.CALLIBOT) ) {
            CALLIBOT_PINS.forEach((v) -> checkIfContainsPin(configurationComponent, currentFreePins, blockPins, v));
        } else if ( configurationComponent.getComponentType().equals(SC.BUZZER) || existingPins.contains(componentProperties.get(PORT)) ) {
            String pin = correctConfigPins.containsKey(componentType) ? correctConfigPins.get(componentType) : componentProperties.get(PORT);
            checkIfContainsPin(configurationComponent, currentFreePins, blockPins, pin);
        } else {
            throw new DbcException("Invalid pin for configuration block " + configurationComponent.getComponentType());
        }
        if ( blockPins.stream().distinct().count() != blockPins.size() ) {
            project.addToErrorCounter(1, null);
            project.setResult(Key.PROGRAM_INVALID_STATEMETNS);
            String blockId = configurationComponent.getProperty().getBlocklyId();
            project.addToConfAnnotationList(blockId, NepoInfo.error("CONFIGURATION_ERROR_OVERLAPPING_PORTS"));
        }
    }

    private void checkIfContainsPin(ConfigurationComponent configurationComponent, List<String> currentFreePins, List<String> blockPins, String pin) {
        if ( currentFreePins.contains(pin) ) {
            blockPins.add(pin);
            currentFreePins.removeIf(s -> s.equals(pin));
            return;
        }
        project.addToErrorCounter(1, null);
        project.setResult(Key.PROGRAM_INVALID_STATEMETNS);
        String blockId = configurationComponent.getProperty().getBlocklyId();
        project.addToConfAnnotationList(blockId, NepoInfo.error("CONFIGURATION_ERROR_OVERLAPPING_PORTS"));
    }
}
