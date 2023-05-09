package com.rbpsc.ctp.api.entities.dto.webview;

import com.rbpsc.ctp.api.entities.supplychain.SupplyChainBaseEntity;
import com.rbpsc.ctp.api.entities.supplychain.drug.DrugLifeCycle;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Document
public class DrugLifeCycleView extends SupplyChainBaseEntity {
    private List<DrugLifeCycle> drugLifeCycleList;
}
