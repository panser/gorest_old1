'use strict';

angular.module('gorestApp')
    .controller('EntryDetailController', function ($scope, $rootScope, $stateParams, DataUtils, entity, Entry) {
        $scope.entry = entity;
        $scope.load = function (id) {
            Entry.get({id: id}, function(result) {
                $scope.entry = result;
            });
        };
        var unsubscribe = $rootScope.$on('gorestApp:entryUpdate', function(event, result) {
            $scope.entry = result;
        });
        $scope.$on('$destroy', unsubscribe);

        $scope.byteSize = DataUtils.byteSize;
    });
