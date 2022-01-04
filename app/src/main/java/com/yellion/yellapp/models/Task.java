package com.yellion.yellapp.models;

import com.squareup.moshi.Json;

import io.realm.RealmObject;

public class Task extends RealmObject {
    @Json(name = "dashboard_id")
    public String dashboard_id;
    @Json(name = "task_id")
    public String task_id;
    @Json(name = "name")
    public String name;
    @Json(name = "status")
    public String status;
    @Json(name = "noti_level")
    public String noti_level;
    @Json(name = "priority")
    public String priority;
    @Json(name = "parent_id")
    public String parent_id;
    @Json(name = "start_time")
    public String start_time;
    @Json(name = "end_time")
    public String end_time;
    @Json(name = "labels")
    public String labels;
    @Json(name = "content")
    public String content;

    public Task() {
    }

    public Task(String dashboard_id, String name,
                String status, String noti_level,
                String priority, String parent_id,
                String start_time, String end_time,
                String labels, String content) {
        this.dashboard_id = dashboard_id;
        this.name = name;
        this.status = status;
        this.noti_level = noti_level;
        this.priority = priority;
        this.parent_id = parent_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.labels = labels;
        this.content = content;
    }

    public String getDashboard_id() {
        return dashboard_id;
    }

    public void setDashboard_id(String dashboard_id) {
        this.dashboard_id = dashboard_id;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNoti_level() {
        return noti_level;
    }

    public void setNoti_level(String noti_level) {
        this.noti_level = noti_level;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getLabels() {
        return labels;
    }

    public void setLabels(String labels) {
        this.labels = labels;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
