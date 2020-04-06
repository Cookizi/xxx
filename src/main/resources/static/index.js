$(function () {

    $("#siteSelect").combobox({
        onChange: function (newValue) {
            $.ajax({
                url: "/get-category",
                data: {site: newValue},
                success: function (data) {
                    data = data.map(x => {
                        return {text: x, value: x}
                    });
                    let cat = $("#categorySelect");
                    cat.combobox({data: data});
                    // cat.combobox("select", data[0]["value"]);
                }
            })
        }
    });
    $("#videoDataTable").datagrid();

    $("#taskTable").datagrid({
        fitColumns: true, singleSelect: true, pagination: true, striped: true,
        pagePosition: "top",
        url: "/get-task",
        method: 'get',
        queryParams: {
            site: $("#siteSelect").val(),
            category: $("#categorySelect").val()
        },
        onClickRow: function (index, data) {

            $("#videoDataTable").datagrid({
                fitColumns: true, singleSelect: true, pagination: true, striped: true,
                pagePosition: "top",
                pageSize: 200, pageList: [100, 200, 400, 800],
                url: "/get-video-data",
                method: 'get',
                queryParams: {taskId: data.id},
            });

        }
    })
    ;


    $.ajax({
        url: "/get-site",
        success: function (data) {
            let site = $("#siteSelect");
            data = data.map(x => {
                return {text: x, value: x}
            });
            site.combobox({data: data});
        }
    });


});

function searchTask() {
    $("#taskTable").datagrid({
        queryParams: {
            site: $("#siteSelect").combobox("getValue"),
            category: $("#categorySelect").combobox("getValue")
        }
    });


}

function taskStatusFmt(value, row, index) {
    if (value === 0) return `<span style="color:darkred;">未完成</span>`;
    if(value === 1) return `<span style="color:green;">已完成</span>`;
    else return `<span style="color:#ffab2e;">等待中</span>`
}

function timeFmt(value, row, index) {
    let date = new Date(value);
    return date.getFullYear() + '/' + (date.getMonth() + 1) + '/' + date.getDate();
}

function taskDownloadFtm(value, row, index) {
    return `<input type="button" onclick="openTask('${row.id}','${row.currentPage}')" value="下载"/>`
}

function importTask() {
    $("#taskInput").val("");

    $("#aira2Type").css("display", "none");
    let taskWindow = $("#importTask");
    taskWindow.dialog({
        title: '导入任务',
        resizable: true,
        modal: true
    });
    $("#saveTask").css("display", "");
    taskWindow.panel("open");
}

function saveTask() {
    let tasks = $("#taskInput").val();
    tasks = tasks.split("\n").filter(x => x.trim() !== '');

    if (tasks.length === 0) {
        $.messager.alert('Warning', '请输入任务连接');
        return;
    }
    $.ajax({
        url: 'save-task',
        data: {taskList: tasks.join(",")},
        contentType: 'application/x-www-form-urlencoded',
        type: 'post',
        success: function () {
            $.messager.alert('Info', '保存成功');
            $("#taskInput").val('');
            $("#taskTable").datagrid("reload");
        }
    })
}

function exportVideo() {
    $("#aira2Type").css("display", "");
    let panel = $('#importTask');
    panel.dialog({
        title: '导出下载地址',
        resizable: true,
        modal: true
    });
    $("#saveTask").css("display", "none");
    panel.panel('open');
    let data = $("#videoDataTable").datagrid("getData");
    let isAria2Type = $("#isAira2Type").prop("checked");

    let path = data.rows.map(row => {
        let x = row.videoPath;
        if (isAria2Type) {
            x = x + " --out=" + row.filename;
        }
        return x;
    }).join("\n");
    $("#taskInput").val(path);
}

function startTask() {
    let param = {};
    param.taskId = $("#taskId").val();
    param.startPage = $("#startPage").val();
    param.endPage = $("#endPage").val();
    $.ajax({
        url: "/start-task",
        method: "post",
        data: param,
        success: function (response) {
            if (response.code === "0") {
                $('#taskPage').panel('close');
                $.messager.alert('Info', '任务启动成功');
            }
        }
    })
}

function openTask(taskId, currentPage) {
    $("#taskId").val(taskId);
    $("#taskPage").panel("open");

    $("#startPage").val(currentPage);
    $("#endPage").val("");

}


function renew() {
    let dataTable = $("#videoDataTable");


    let data = dataTable.datagrid("getRows");

    //updateRow
    //$('#dg').datagrid('updateRow',{
    // 	index: 2,
    // 	row: {
    // 		name: 'new name',
    // 		note: 'new note message'
    // 	}
    // });

    for (let i = 0; i < data.length; i++) {
        dataTable.datagrid("updateRow", {
            index: i, row: {status:""}
        })
    }
    for (let i = 0; i < data.length; i++) {
        let d = data[i];
        $.ajax({
            url: "renew",
            type: "post",
            data: {videoId: d.id},
            // async:false,
            success: function (response) {
                if (response.code === 1) {
                    dataTable.datagrid("updateRow", {
                        index: i, row: response.data
                    })
                }
            }
        })
    }

    console.log(data);

}

