$(".check").uniform();
$(function() {
    $( "#fromDate" ).datepicker();
    $( "#fromDate" ).datepicker("option", "dateFormat", "dd-mm-yy");
    $( "#untilDate").datepicker();
    $( "#untilDate").datepicker("option", "dateFormat", "dd-mm-yy");
});

$('#fromDate').datepicker({
    onSelect: function(){
        $(this).blur();
    }
})

$('#untilDate').datepicker({
    onSelect: function(){
        $(this).blur();
    }
})

$('#from-btn').click(function(){
    $('#fromDate').datepicker('show');
})

$('#until-btn').click(function(){
    $('#untilDate').datepicker('show');
})

$('#low-budget').click(function(){
    $('#slider-range').slider({
        range: true,
        min: 0,
        max: 1.01,
        step: 0.1,
        values: [ 0, 1 ],
        slide: function( event, ui ) {
            $( "#min-amount" ).val(ui.values[ 0 ]);
            $( "#max-amount" ).val(ui.values[ 1 ]);
        },
        stop: function( event, ui){
            $('#min-amount').blur();
            $('#max-amount').blur();
        }
    })

    $('#min-amount').blur();
    $('#max-amount').blur();

    $( "#min-amount" ).val($( "#slider-range" ).slider( "values", 0 ));
    $( "#max-amount" ).val($( "#slider-range" ).slider( "values", 1 ));
})

$('#high-budget').click(function(){
    $('#slider-range').slider({
        range: true,
        min: 0,
        max: 1500,
        step: 10,
        values: [ 0, 1500 ],
        slide: function( event, ui ) {
            $( "#min-amount" ).val(ui.values[ 0 ]);
            $( "#max-amount" ).val(ui.values[ 1 ]);
        },
        stop: function( event, ui){
            $('#min-amount').blur();
            $('#max-amount').blur();
        }
    })

    $('#min-amount').blur();
    $('#max-amount').blur();

    $( "#min-amount" ).val($( "#slider-range" ).slider( "values", 0 ));
    $( "#max-amount" ).val($( "#slider-range" ).slider( "values", 1 ));
})

function startLoad(){
    $(".posts .row").css('display', 'none');
    $(".innerPosts .posts").append("<img src='images/ajax-loader.gif' class='loader'>");
};

function stopLoad(){
    $(".posts .row").css('display', 'block');
    $(".loader").remove();
}

$('.cross').click(function(){
    $('.warning').remove();
})

function removeWarn(){
    $('.warning').remove();
}

function setMaxBudget(budg){
    function stepCounter(budg_number){
        if(budg_number < 10000)
            return 10
        if(budg_number > 10000 && budg_number < 100000)
            return 100
        if(budg_number > 100000)
            return 1000
    }

    var step = stepCounter(budg);
    console.log(budg);
    console.log(step);
    $('#slider-range').slider({
        range: true,
        min: 0,
        max: budg,
        step: step,
        values: [ 0, budg ],
        slide: function( event, ui ) {
            $( "#min-amount" ).val(ui.values[ 0 ]);
            $( "#max-amount" ).val(ui.values[ 1 ]);
        },
        stop: function( event, ui){
            $('#min-amount').blur();
            $('#max-amount').blur();
        }
    })

    $( "#min-amount" ).val($( "#slider-range" ).slider( "values", 0 ));
    $( "#max-amount" ).val($( "#slider-range" ).slider( "values", 1 ));
}

setMaxBudget(1500);