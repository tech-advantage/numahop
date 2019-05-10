(function () {
  "use strict";

  angular.module('numaHopApp')
    .factory('DateUtils', function () {
      var service = {};

      service.convertLocaleDateToServer = function (date) {
        if (date) {
          var utcDate = new Date();
          utcDate.setUTCDate(date.getDate());
          utcDate.setUTCMonth(date.getMonth());
          utcDate.setUTCFullYear(date.getFullYear());
          return utcDate;
        } else {
          return null;
        }
      };
      service.convertLocaleDateFromServer = function (date) {
        if (date) {
          var dateString = date.split("-");
          return new Date(dateString[0], dateString[1] - 1, dateString[2]);
        }
        return null;
      };
      service.convertDateTimeFromServer = function (date) {
        if (date) {
          return new Date(date);
        } else {
          return null;
        }
      };
      
      service.getFormattedDateYearMonthDayHourMinSec = function(date) {
          if (date) {
              var mm = (date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1);
              var dd = date.getDate() < 10 ?  "0"+ date.getDate() : date.getDate();
              var hh = date.getHours() < 10 ?  "0"+ date.getHours() : date.getHours();
              var mn = date.getMinutes() < 10 ?  "0"+ date.getMinutes() : date.getMinutes();
              var ss = date.getSeconds() < 10 ?  "0"+ date.getSeconds() : date.getSeconds();
              
              return ""+ date.getFullYear() + "-" + mm + "-" + dd 
                       + " " 
                       + hh + "-" + mn + "-" + ss;
          } else {
              return null;
          }
      }

      return service;
    });
})();