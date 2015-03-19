/// <reference path='_all.ts' />

module wasteland2 {
    'use strict';

    class WeaponController {

        static $inject = ['$http'];

        weapons = []

        constructor(
            private $http: ng.IHttpService
            ) {
            $http.get('./wasteland2/weapons/last/10').success((data) => this.weapons = <any[]>data);
        }
    }

    var app = angular.module('app', []);
    app.controller('WeaponController', WeaponController);
}
