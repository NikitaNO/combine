angular.module('LimoApp')
    .controller('MainController', ['$scope', '$resource','Main', '$http', function($scope, $resource, Main, $http){
        $scope.data = {};
        $scope.postsNumber = 25;
        var convertFromDate = function(){
            if($scope.data.fromDate)
                if(typeof $scope.data.fromDate != 'string'){
                    $scope.data.fromDate = "" + $scope.data.fromDate.getFullYear() + "-" + ($scope.data.fromDate.getMonth()+1) + "-"  + $scope.data.fromDate.getDate();
                }
        }

        var convertUntilDate = function(){
            if($scope.data.untilDate)
                if(typeof $scope.data.untilDate != 'string'){
                    $scope.data.untilDate = "" + $scope.data.untilDate.getFullYear() + "-" + ($scope.data.untilDate.getMonth()+1) + "-"  + $scope.data.untilDate.getDate();
                }
        }

//        var refresh = function(data){
//            Main.get(
//                data
//            ,
//            function(msms){
//                $scope.items = items.posts;
//            },
//                function(err){
//                    console.log(err);
//                }
//            )
//        }

//        refresh({feed: 'Weblancer, Advego'});

        $scope.today = function() {
            $scope.from = new Date();
            $scope.to = new Date();
        };

        $scope.today();

        $scope.clear = function () {
            $scope.from = null;
            $scope.to = null;
        };

        // Disable weekend selection
        $scope.disabled = function(date, mode) {
            return ( mode === 'day' && ( date.getDay() === 0 || date.getDay() === 6 ) );
        };

        $scope.toggleMin = function() {
            $scope.minDate = $scope.minDate ? null : new Date();
        };
        $scope.toggleMin();

        $scope.openFrom = function($event) {
            $scope.openedTo = false;
            $event.preventDefault();
            $event.stopPropagation();

            $scope.openedFrom = true;
        };

        $scope.openTo = function($event) {
            $scope.openedFrom = false;
            $event.preventDefault();
            $event.stopPropagation();

            $scope.openedTo = true;
        };

        $scope.dateOptions = {
            formatYear: 'yy',
            startingDay: 1
        };

        $scope.formats = ['dd-MMMM-yyyy', 'yyyy/MM/dd', 'yyyy-MM-dd', 'shortDate'];
        $scope.format = $scope.formats[2];

        $scope.search = function() {
            convertFromDate();
            convertUntilDate();
//            refresh($scope.data);
        }

        //Pagination

        $scope.currentPage = 0;
        $scope.pagination = [];
        $scope.counter = [];
        $scope.activePage = 'active';
        $scope.actives = [];

        var PAGES_AMOUNT = 11;
        var rightSide;
        var leftSide;
        if(PAGES_AMOUNT > 2) {
            if (PAGES_AMOUNT % 2 != 0) {
                rightSide = (PAGES_AMOUNT - 1) / 2;
                leftSide = rightSide;
            }
            else {
                rightSide = PAGES_AMOUNT / 2;
                leftSide = rightSide;
            }
        }

        var Post = $resource('/service/jobsTotal',
            {
            },
            {
                get: {
                    method: 'GET',
                    isArray: false
                }
            }
        );


        var Posts = $resource('/service/jobs/:feed',
            {
            },
            {
                get: {
                    method: 'GET',
                    isArray: false
                }
            }
        );

        $scope.filterSearch = function(){
            convertFromDate();
            convertUntilDate();
            Posts.get(
                $scope.data,
                function(success){
                    $scope.items = success.posts;
                    setPagesAmount(success.posts.length);
                    setNearPages(0);
                    console.log(success);
                },
                function(err){
                    console.log(err);
                }
            )
        }

        var refreshPostsNumber = function(page) {
            Post.get(
                {
                },
                function (success) {
//                    console.log(Math.ceil(success.total / $scope.postsNumber));
                    console.log(success.total);
                    setPagesAmount(success.total);
                    setNearPages(page);
                },
                function (error) {
                    console.log(error);
                }
            );
        }

        var refresh = function(page) {
            Posts.get(
                {
                    fromPost: $scope.postsNumber * page,
                    untilPost: $scope.postsNumber * (page + 1)
                },
                function (success) {
                    $scope.items = success.posts;
                    console.log(success);
                    refreshPostsNumber(page);
//                    setNearPages(page);
                },
                function (error) {
                    console.log(error);
                }
            );
        }

        $scope.refreshPostsNumberView = function(){
            refreshPostsNumber(0);
            refresh(0);
        }

        refresh(0);

        var setPagesAmount = function(amount){
            var n = Math.ceil(amount/$scope.postsNumber);
            if(n < $scope.counter.length){
                $scope.counter.length = n;
                if(PAGES_AMOUNT > n) {
                    $scope.pagination.length = n;
                }
            }

            for(var i = 0; i < n; i++){
                $scope.counter[i] = i;
            }

            $scope.pageNumber = $scope.counter.length - 1;
            $scope.actives = [$scope.pagination.length];

        }

        var setNearPages = function(currentPage) {
            var i, j;
            console.log(currentPage);
            console.log($scope.pageNumber);
            j = $scope.currentPage;

            if(PAGES_AMOUNT > $scope.pageNumber){
                var temp = $scope.pageNumber + 1
                for(i=0; i < temp; i++){
                    $scope.pagination[i] = i;
                }
            }
            else {

                if (currentPage == 0 || currentPage <= leftSide) {
                    for (i = 0; i < PAGES_AMOUNT; i++) {
                        $scope.pagination[i] = i;
                    }
                }

                if (currentPage == $scope.pageNumber) {
                    j = currentPage + 1;
                    j -= PAGES_AMOUNT;
                    for (i = 0; i < PAGES_AMOUNT; i++) {
                        $scope.pagination[i] = j;
                        j++;
                    }
                }

                if (currentPage > leftSide && currentPage < $scope.pageNumber && currentPage < ($scope.pageNumber - rightSide)) {
                    j = currentPage;
                    j -= leftSide;
                    for (i = 0; i < PAGES_AMOUNT; i++) {
                        $scope.pagination[i] = j;
                        j++;
                    }
                }

                if (currentPage > ($scope.pageNumber - rightSide) && currentPage != $scope.pageNumber) {
                    j = $scope.pageNumber + 1;
                    j -= PAGES_AMOUNT;
                    for (i = 0; i < PAGES_AMOUNT; i++) {
                        $scope.pagination[i] = j;
                        j++;
                    }
                }
            }
            $scope.actives[currentPage] = 'active'
        }

        $scope.setActive = function(){
            for(i=0; i<$scope.actives.length; i++){
                if($scope.actives[i] == 'active')
                    $scope.actives[i] = ''
            }
        }

        $scope.selectPage = function(index){
            console.log(index);
            $scope.currentPage = index;
            $scope.setActive();
            refresh(index);
        }

        $scope.firstPage = function(){
            $scope.currentPage = 0;
            $scope.setActive();
            refresh($scope.currentPage);
        }

        $scope.lastPage = function(){
            $scope.currentPage = $scope.pageNumber;
            $scope.setActive();
            refresh($scope.currentPage);
        }

        $scope.nextPage = function(){
            console.log($scope.currentPage);
            if($scope.currentPage < $scope.pageNumber) {
                $scope.currentPage +=1;
                $scope.setActive();
                refresh($scope.currentPage);
            }
        }

        $scope.prevPage = function(){
            console.log($scope.currentPage);
            if($scope.currentPage != 0) {
                $scope.currentPage -= 1;
                $scope.setActive();
                refresh($scope.currentPage);
            }
        }

    }])