package com.phicomm.smartplug.modules.scene.utils;

import com.phicomm.smartplug.R;
import com.phicomm.smartplug.modules.data.remote.beans.scene.CreateSceneRequestBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.EditSceneRequestBean;
import com.phicomm.smartplug.modules.data.remote.beans.scene.GetSceneDetailResponseBean;
import com.phicomm.smartplug.modules.scene.model.ControlModel;
import com.phicomm.smartplug.modules.scene.model.DeviceModel;
import com.phicomm.smartplug.modules.scene.model.OperationModel;
import com.phicomm.smartplug.modules.scene.model.SceneModel;
import com.phicomm.smartplug.modules.scene.model.TaskModel;
import com.phicomm.smartplug.modules.scene.model.TimeModel;
import com.phicomm.smartplug.modules.scene.model.TriggerModel;

import java.util.ArrayList;
import java.util.List;

public class ConvertUtils {
    public static CreateSceneRequestBean convertSenceToCreateRequestBean(SceneModel scene) {
        CreateSceneRequestBean createSceneRequestBean = new CreateSceneRequestBean();

        // set scene property
        createSceneRequestBean.setAuto(scene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER);
        createSceneRequestBean.setEnable(scene.isEnabled());
        createSceneRequestBean.setScenario_description(scene.getSceneName());
        createSceneRequestBean.setScenario_name(scene.getSceneName());
        if (scene.getTrigger().getTriggerType() == TriggerModel.TRIGGER_TIMER) {
            createSceneRequestBean.setTrigger_alarm(scene.getTrigger().getTriggerTime().getTimeStream());
        } else {
            createSceneRequestBean.setTrigger_alarm("");
        }

        // set task property
        List<TaskModel> sceneTasks = scene.getTasks();
        List<CreateSceneRequestBean.TaskBean> beanTasks = new ArrayList<>();
        CreateSceneRequestBean.TaskBean beanTask;
        for (TaskModel sceneTask : sceneTasks) {
            beanTask = new CreateSceneRequestBean.TaskBean();
            beanTask.setControl_id(sceneTask.getDevice().getCurrentControl().getControlId());
            beanTask.setDev_id(sceneTask.getDevice().getDeviceId());
            beanTask.setTask_description(sceneTask.getDevice().getCurrentControl().getControlName());
            beanTask.setTask_id(sceneTask.getTaskId());
            beanTask.setTask_value(sceneTask.getTaskValue());
            beanTask.setTime_delay_hour(sceneTask.getDevice().getCurrentControl().getDelayTime().getHour());
            beanTask.setTime_delay_minute(sceneTask.getDevice().getCurrentControl().getDelayTime().getMinute());

            beanTasks.add(beanTask);
        }

        createSceneRequestBean.setTask_list(beanTasks);

        return createSceneRequestBean;
    }


    public static EditSceneRequestBean convertSenceToEditRequestBean(SceneModel scene) {
        EditSceneRequestBean editSceneRequestBean = new EditSceneRequestBean();

        // set scene property
        editSceneRequestBean.setAuto(scene.getTrigger().getTriggerType() == 1);
        editSceneRequestBean.setEnable(scene.isEnabled());
        editSceneRequestBean.setScenario_description(scene.getSceneName());
        editSceneRequestBean.setScenario_name(scene.getSceneName());
        editSceneRequestBean.setTrigger_alarm(scene.getTrigger().getTriggerTime().getTimeStream());
        editSceneRequestBean.setScenario_id(scene.getSceneId());

        // set task property
        List<TaskModel> sceneTasks = scene.getTasks();
        List<EditSceneRequestBean.TaskBean> beanTasks = new ArrayList<>();
        EditSceneRequestBean.TaskBean beanTask;
        for (TaskModel sceneTask : sceneTasks) {
            beanTask = new EditSceneRequestBean.TaskBean();
            beanTask.setControl_id(sceneTask.getDevice().getCurrentControl().getControlId());
            beanTask.setDev_id(sceneTask.getDevice().getDeviceId());
            beanTask.setDev_name(sceneTask.getDevice().getDeviceName());
            beanTask.setTask_description(sceneTask.getDevice().getCurrentControl().getControlName());
            beanTask.setTask_id(sceneTask.getTaskId());
            beanTask.setTask_value(sceneTask.getTaskValue());
            beanTask.setTime_delay_hour(sceneTask.getDevice().getCurrentControl().getDelayTime().getHour() + "");
            beanTask.setTime_delay_minute(sceneTask.getDevice().getCurrentControl().getDelayTime().getMinute() + "");
            beanTasks.add(beanTask);
        }

        editSceneRequestBean.setTask_list(beanTasks);

        return editSceneRequestBean;
    }

    public static SceneModel convertSceneDetailBeanToSceneModel(GetSceneDetailResponseBean bean) {
        SceneModel scene = new SceneModel();

        // scene detail
        scene.setStatus(bean.getScenario().getScenario_status());
        scene.setSceneDescription(bean.getScenario().getScenario_description());
        scene.setUid(Long.decode(bean.getScenario().getUid()));
        scene.setLastUpdateTime(bean.getScenario().getLastUpdateTime());
        scene.setLastExecuteTime(bean.getScenario().getLastExecuteTime());
        scene.setSceneName(bean.getScenario().getScenario_name());
        scene.setEnabled(bean.getScenario().getEnable());
        scene.setSceneId(bean.getScenario().getScenario_id());
        scene.setRunning(bean.getScenario().is_running());

        // trigger detail
        TimeModel time = new TimeModel();
        time.parseTimeStream(bean.getScenario().getTrigger_alarm());
        TriggerModel trigger = new TriggerModel();
        trigger.setTriggerTime(time);
        trigger.setTriggerDescription(bean.getScenario().getAuto() ? R.string.trigger_timer : R.string.trigger_manually);
        trigger.setImageId(bean.getScenario().getAuto() ? R.drawable.scene_timer_trigger_icon : R.drawable.scene_manually_trigger_icon);
        trigger.setTriggerType(bean.getScenario().getAuto() ? 1 : 0);
        scene.setTrigger(trigger);

        // task detail
        List<TaskModel> tasks = new ArrayList<>();
        List<GetSceneDetailResponseBean.ScenarioBean.TaskListBean> beanTasks = bean.getScenario().getTask_list();
        for (GetSceneDetailResponseBean.ScenarioBean.TaskListBean beantask : beanTasks) {
            TaskModel task = new TaskModel();
            TimeModel taskTime = new TimeModel(beantask.getTime_delay_hour(), beantask.getTime_delay_minute());

            ControlModel control = convertTaskValueToControlOperation(beantask.getTask_value());
            control.setDelayTime(taskTime);
            control.setControlName(beantask.getControl_name());

            DeviceModel device = new DeviceModel();
            device.setCurrentControl(control);
            device.setDeviceId(beantask.getDev_id());
            device.setDeviceName(beantask.getDev_name());
            device.setImageId(R.drawable.device_default_icon);
            task.setDevice(device);

            task.setSceneId(beantask.getScenario_id());
            task.setLastExecuteTime(beantask.getLastExecuteTime());
            task.setResultCode(beantask.getResultCode());
            task.setCurrentstate(beantask.getCurrentstate());
            task.setLastUpdateTime(beantask.getLastUpdateTime());
            task.setTaskDescription(beantask.getTask_description());
            task.setTaskId(beantask.getTask_id());
            task.setTaskMessage(beantask.getTask_message());
            tasks.add(task);
        }

        scene.setTasks(tasks);
        return scene;
    }

    private static ControlModel convertTaskValueToControlOperation(int taskValue) {
        int operation = OperationModel.NO_CHANGE;
        int controlId = -1;
        for (int i = 3; i >= 0; i--) {
            operation = taskValue / ((int) Math.pow(10, i));

            if (operation != OperationModel.NO_CHANGE) {
                controlId = i;
                break;
            } else {
                taskValue -= OperationModel.NO_CHANGE * ((int) Math.pow(10, i));
            }
        }

        OperationModel operationModel = new OperationModel();
        operationModel.setOperation(operation);

        ControlModel controlModel = new ControlModel();
        controlModel.setControlId(controlId);
        controlModel.setCurrentOperation(operationModel);

        return controlModel;
    }
}
