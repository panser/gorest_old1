'use strict';

angular.module('gorestApp').controller('EntryDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Entry',
        function($scope, $stateParams, $uibModalInstance, DataUtils, entity, Entry) {

        $scope.entry = entity;
        $scope.load = function(id) {
            Entry.get({id : id}, function(result) {
                $scope.entry = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('gorestApp:entryUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.entry.id != null) {
                Entry.update($scope.entry, onSaveSuccess, onSaveError);
            } else {
                Entry.save($scope.entry, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        $scope.abbreviate = DataUtils.abbreviate;

        $scope.byteSize = DataUtils.byteSize;
        $scope.datePickerForDate = {};

        $scope.datePickerForDate.status = {
            opened: false
        };

        $scope.datePickerForDateOpen = function($event) {
            $scope.datePickerForDate.status.opened = true;
        };
}]);
