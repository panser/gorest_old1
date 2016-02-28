'use strict';

angular.module('gorestApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


