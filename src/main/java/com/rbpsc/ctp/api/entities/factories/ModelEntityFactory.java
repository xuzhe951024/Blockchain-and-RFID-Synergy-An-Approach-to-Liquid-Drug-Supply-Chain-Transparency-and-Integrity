package com.rbpsc.ctp.api.entities.factories;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rbpsc.ctp.api.entities.configs.ExperimentConfig;
import com.rbpsc.ctp.api.entities.dto.OperationDTO;
import com.rbpsc.ctp.api.entities.dto.webview.DrugLifeCycleVO;
import com.rbpsc.ctp.api.entities.dto.webview.SimulationDataView;
import com.rbpsc.ctp.api.entities.supplychain.drug.DrugInfo;
import com.rbpsc.ctp.api.entities.supplychain.drug.DrugLifeCycle;
import com.rbpsc.ctp.api.entities.supplychain.operations.DrugOrderStep;
import com.rbpsc.ctp.api.entities.supplychain.operations.OperationBase;
import com.rbpsc.ctp.api.entities.supplychain.roles.Consumer;
import com.rbpsc.ctp.api.entities.supplychain.roles.Institution;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.rbpsc.ctp.common.Constant.EntityConstants.DEFAULT_OPERATION_MSG;

public class ModelEntityFactory {
    public static List<DrugLifeCycle> createDrugLifeCycleView(ExperimentConfig experimentConfig, List<Consumer> consumerList, List<List<Institution>> institutionTree) throws JsonProcessingException {
        DrugLifeCycleVO drugLifeCycleVO = new DrugLifeCycleVO();
        drugLifeCycleVO.setBatchId(experimentConfig.getExperimentName());
        DataEntityFactory.setId(drugLifeCycleVO);
        Random random = new Random();

        int numberOfDrugs = experimentConfig.getConsumerCount() * experimentConfig.getDoesForEachConsumer();
        String drugName = experimentConfig.getDrugName();
        List<DrugLifeCycle> drugLifeCycleList = new ArrayList<>();
        for (int i = 0; i < numberOfDrugs; i++) {
            DrugInfo drugInfo = DataEntityFactory.createDrugInfo(drugLifeCycleVO, drugName);

            int levelNum = experimentConfig.getDistributorsForEachLevel().size();
            List<OperationDTO> operationDTOQueue = new ArrayList<>();
            for (int j = 0; j < levelNum; j++) {
                int randInt = random.nextInt(experimentConfig.getDistributorsForEachLevel().get(j));
                List<Institution> institutionList = institutionTree.get(j);

                DrugOrderStep drugOrderStep = DataEntityFactory.createDrugOrderStep(institutionList.get(randInt), DEFAULT_OPERATION_MSG);

                OperationDTO operationDTO = new OperationDTO();
                operationDTO.setId(UUID.randomUUID().toString());
                operationDTO.setOperationType(drugOrderStep.getClass().getName());
                operationDTO.setOperation(drugOrderStep);

                operationDTOQueue.add(operationDTO);
            }

            DrugLifeCycle drugLifeCycle = DataEntityFactory.createDrugLifeCycle(drugLifeCycleVO, drugInfo);
            drugLifeCycle.setOperationDTOQueue(operationDTOQueue);
            drugLifeCycle.setExpectedReceiver(consumerList.get(i / experimentConfig.getDoesForEachConsumer()));
            drugLifeCycle.setTagTagId(UUID.randomUUID().toString());
            drugLifeCycleList.add(drugLifeCycle);
        }

        return drugLifeCycleList;
    }

    public static List<DrugLifeCycle> buildDrugLifeCycleFromVOList(List<DrugLifeCycleVO> drugLifeCycleVOList, String batchId) {
        SimulationDataView simulationDataView = new SimulationDataView();
        simulationDataView.setId(UUID.randomUUID().toString());
        simulationDataView.setBatchId(batchId);

        List<DrugLifeCycle> drugLifeCycleList = new ArrayList<DrugLifeCycle>() {{
            drugLifeCycleVOList.forEach(drugLifeCycleVO -> {
                Consumer consumer = DataEntityFactory.createConsumer(drugLifeCycleVO.getExpectedDose(), drugLifeCycleVO.getTargetConsumer());

                DrugInfo drugInfo = DataEntityFactory.createDrugInfo(simulationDataView, drugLifeCycleVO.getDrugName());
                drugInfo.setId(drugLifeCycleVO.getDrugId());

                List<OperationDTO> operationDTOList = new ArrayList<OperationDTO>() {{
                    DrugOrderStep consumerStep = DataEntityFactory.createDrugOrderStep(consumer, DEFAULT_OPERATION_MSG);
                    OperationDTO operationDTO = DataEntityFactory.createOperationDTO(consumerStep, consumerStep.getClass().getName());
                    add(operationDTO);
                    drugLifeCycleVO.getOperationVOList().forEach(operationVO -> {
                        OperationBase operationBase = new OperationBase();
                        operationBase.setOperationMSG(operationVO.getOperationMsg());
                        String address = operationVO.getOperatorAdd();
                        operationBase.setAddress(address);

                        String[] name = address.split("\\.");
                        operationBase.setRoleName(name[name.length - 1]);

                        OperationDTO operationDTOFromPage = DataEntityFactory.createOperationDTO(operationBase, operationVO.getOperationType());
                        add(operationDTOFromPage);
                    });
                }};

                DrugLifeCycle drugLifeCycle = DataEntityFactory.createDrugLifeCycle(simulationDataView, drugInfo);
                drugLifeCycle.setExpectedReceiver(consumer);
                drugLifeCycle.setOperationDTOQueue(operationDTOList);
                drugLifeCycle.setDrug(drugInfo);

                add(drugLifeCycle);
            });
        }};

        return drugLifeCycleList;
    }
}