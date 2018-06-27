/*!
 * Remark (http://getbootstrapadmin.com/remark)
 * Copyright 2015 amazingsurge
 * Licensed under the Themeforest Standard Licenses
 */
function cellStyle(value, row, index) {
  var classes = ['active', 'success', 'info', 'warning', 'danger'];

  if (index % 2 === 0 && index / 2 < classes.length) {
    return {
      classes: classes[index / 2]
    };
  }
  return {};
}

function rowStyle(row, index) {
  var classes = ['active', 'success', 'info', 'warning', 'danger'];

  if (index % 2 === 0 && index / 2 < classes.length) {
    return {
      classes: classes[index / 2]
    };
  }
  return {};
}

function scoreSorter(a, b) {
  if (a > b) return 1;
  if (a < b) return -1;
  return 0;
}

function nameFormatter(value) {
  return value + '<i class="icon wb-book" aria-hidden="true"></i> ';
}

function starsFormatter(value) {
  return '<i class="icon wb-star" aria-hidden="true"></i> ' + value;
}

function queryParams() {
  return {
    type: 'owner',
    sort: 'updated',
    direction: 'desc',
    per_page: 100,
    page: 1
  };
}

function buildTable($el, cells, rows) {
  var i, j, row,
    columns = [],
    data = [];

  for (i = 0; i < cells; i++) {
    columns.push({
      field: '字段' + i,
      title: '单元' + i
    });
  }
  for (i = 0; i < rows; i++) {
    row = {};
    for (j = 0; j < cells; j++) {
      row['字段' + j] = 'Row-' + i + '-' + j;
    }
    data.push(row);
  }
  $el.bootstrapTable('destroy').bootstrapTable({
    columns: columns,
    data: data,
    iconSize: 'outline',
    icons: {
      columns: 'glyphicon-list'
    }
  });
}

window.operateEvents = 
{
	'click .opt_cancel': function (e, value, row, index) {
		 swal({
             title: "您确定要取消当前任务吗?",
             type: "warning",
             showCancelButton: true,
             confirmButtonColor: "#DD6B55",
             confirmButtonText: "取消当前任务",
             closeOnConfirm: false
         }, function () {
             swal("取消成功！", "您已经取消了该任务。", "success");
         });          
	},
    'click .opt_retry': function (e, value, row, index) {
    	swal({
            title: "您确定要重新尝试该任务吗?",
            type: "warning",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "重试该任务",
            closeOnConfirm: false
        }, function () {
            swal("重试成功！", "您已经重新启动该任务。", "success");
        });
    },
    'click .opt_delete': function (e, value, row, index) {
    	swal({
            title: "您确定要删除该任务吗?",
            type: "warning",
            text: "删除后将无法恢复，请谨慎操作！",
            showCancelButton: true,
            confirmButtonColor: "#DD6B55",
            confirmButtonText: "确认删除",
            closeOnConfirm: false
        }, function () {
            swal("删除成功！", "您已经删除了当前选中的任务。", "success");
        });
    }
};

function downloadExcelData(fileName){
	$("#form_download").attr("action", "/task/downloadExecelData?fileName="+fileName);
	$("#form_download").submit();
};

function initSubTable(index,row,$detail) {
	var data = [];
	if(row.record_data == null || row.record_data == ""){
		data.push(JSON.parse("{\"Task Status\":\"当前正在处理中......\"}"));
	} else {
		data.push(JSON.parse(row.record_data));
	}
	var columns = [];
	for(var key in data[0]){
		var tmp = {};
		tmp.field=key;
		tmp.title=key;
		columns.push(tmp);
	}
    var cur_table = $detail.html('<table></table>').find('table');  
    $(cur_table).bootstrapTable({  
        data:data,
        cardView:true,
        columns:columns
    }); 
}

(function(document, window, $) {
  'use strict';
  (function() {
    $('#exampleTableToolbar').bootstrapTable({
      method:'get',
      url: "/task/getAllTask",
      contentType: "application/x-www-form-urlencoded",
      cache: false,
      striped: true,
      pagination: true,
      sortable: false,
      pageNumber:1,
      maintainSelected:true,
      pageSize: 10,
      pageList: [10, 25, 50, 100],
      sortOrder: "asc",
      sidePagination: "client",
      search: true,
      showRefresh: true,
      showToggle: true,
      showColumns: true,
      detailView:true,
      showExport: true,
      toolbar: '#toolbar',
      iconSize: 'outline',      
      icons: {
        refresh: 'glyphicon-repeat',
        toggle: 'glyphicon-list-alt',
        paginationSwitchDown: 'glyphicon-collapse-down icon-chevron-down',
        paginationSwitchUp: 'glyphicon-collapse-up icon-chevron-up',
        columns: 'glyphicon-th icon-th',
        detailOpen: 'glyphicon-plus icon-plus',
        detailClose: 'glyphicon-minus icon-minus',
        export: 'glyphicon-export icon-share'
      },
      buttonsAlign:"right",
      exportDataType: "all",
      exportTypes:['excel'],
      exportOptions:{  
          ignoreColumn: [0,1],  //忽略某一列的索引  
          fileName: '签证申请表',  //文件名称设置  
          worksheetName: 'sheet1',  //表格工作区名称  
          tableName: '签证申请表',  
          excelstyles: ['background-color', 'color', 'font-size', 'font-weight'],
      },
      exportHiddenColumns: ["record_data"],
      columns: [{
    	  checkbox: true
      },{
          field: 'id',
          title: 'ID',
          align: 'center'
      },{
          field: 'name',
          title: '姓名',
          align: 'center'
      }, {
          field: 'country',
          title: '申请国家',
          align: 'center'
      }, {
          field: 'status',
          title: '当前状态',
          align: 'center'
      }, {
          field: 'readable_time',
          title: '提交时间',
          align: 'center'
      }, {
          field: 'record_data',
          title: '结果信息',
          align: 'center',
          visible: false
      },/*{
          field: '',
          title: '操作',
          align: 'center',
          events: operateEvents,
          formatter:function(value,row,index){
        	  return [
        	          '<button type="button" class="opt_cancel btn btn-primary btn-xs btn-warning" style="margin-right:10px;"><span style="font-size:10px;">取消</span></button>',
        	          '<button type="button" class="opt_retry btn btn-primary  btn-xs btn-success" style="margin-right:10px;"><span style="font-size:10px;">重试</span></button>',
        	          '<button type="button" class="opt_delete btn btn-primary btn-xs btn-danger" style="margin-right:10px;"><span style="font-size:10px;">删除</span></button>'
        	          ].join('');
          } 
      }*/],
      onExpandRow : function (index, row, $detail) {
    	  initSubTable(index,row,$detail); 
      }
    });
  })();
})(document, window, jQuery);
