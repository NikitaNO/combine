angular.module('LimoApp')
    .factory('Main', ['$resource', function($resource){
        return $resource(
            'service/jobs/:feed',
            {
            },
            {
                get:{
                    method: 'GET',
                    isArray: false
                },

                query:{
                    method: 'GET',
                    isArray: true
                }
            })
    }])