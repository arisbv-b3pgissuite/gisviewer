B3PGissuite.defineComponent('LegendComponent', {
    extend: 'ViewerComponent',
    defaultOptions: {
        formid: 'volgordeForm',
        orderboxId: 'orderLayerBox',
        sliderBoxId: 'transSlider',
        sliderId: 'slider',
        useSortableFunction: true,
        layerDelay: 5000,
        taboptions: {
            resizableContent: true
        }
    },
    reloadTimer: null,
    orderContainer: null,
    constructor: function LegendComponent(options) {
        this.callParent(options);
        this.init();
    },
    init: function() {
        this.component = jQuery('<form></form>').attr({ 'id': this.options.formId });

        this.orderContainer = jQuery('<div></div>').attr({ 'id': this.options.orderboxId, 'class': this.options.orderboxId + ' tabvak_groot' });
        this.component.append(this.orderContainer);

        var slider = jQuery('<div></div>').attr({
            'id': this.options.sliderBoxId
        }).css({
            'height': '50px',
            'width': '260px',
            'padding-left': '5px'
        });
        slider.append(jQuery('<p></p>').text('Transparantie van alle voorgrondlagen.'));
        slider.append(jQuery('<div></div>').text('-').css({
            'float': 'left',
            'font-size': '22px',
            'padding-right': '3px',
            'margin-top': '-8px'
        }));
        slider.append(jQuery('<div></div>').attr({
            'id': this.options.sliderId
        }).css({
            'width': '215px',
            'float': 'left'
        }));
        slider.append(jQuery('<div></div>').text('+').css({
            'float': 'left',
            'font-size': '22px',
            'padding-left': '10px',
            'margin-top': '-6px'
        }));
        slider.append(jQuery('<div></div>').css({ 'clear': 'both' }));
        this.component.append(slider);

        if(this.options.useSortableFunction) {
            this.component.append(jQuery('<p></p>').text('Bepaal de volgorde waarin de kaartlagen getoond worden'));
            var knoppenContainer = jQuery('<div></div>');
            knoppenContainer.append(jQuery('<input />').attr({
                type: 'button',
                value: 'Omhoog',
                'class': 'knop'
            }).click(function() {
                moveSelectedUp();
            }));
            knoppenContainer.append(jQuery('<input />').attr({
                type: 'button',
                value: 'Omlaag',
                'class': 'knop'
            }).click(function() {
                moveSelectedDown();
            }));
            knoppenContainer.append(jQuery('<input />').attr({
                type: 'button',
                value: 'Herladen',
                'class': 'knop'
            }).click(function() {
                refreshMapVolgorde();
            }));
            this.component.append(knoppenContainer);
        }
    },
    afterRender: function() {
        jQuery("#" + this.options.sliderId).slider({
            min: 0,
            max: 100,
            value: 100,
            animate: true,
            change: function(event, ui) {
                var opacity = 1.0 - (ui.value)/100;
                var layers = B3PGissuite.vars.webMapController.getMap().getLayers();
                for( var i = 0 ; i < layers.length ; i++ ){
                    var l = layers[i];
                    if(!l.getOption("background")) {
                        l.setOpacity (opacity);
                    }
                }
            }
        });
        if(this.options.useSortableFunction) {
            this.orderContainer.sortable({
                stop: function() {
                    setTimerForReload();
                },
                start: function() {
                    clearTimerForReload();
                }
            });
        }
    },
    setTimerForReload: function() {
        this.reloadTimer = setTimeout("refreshMapVolgorde()", this.options.layerDelay);
    },
    clearTimerForReload: function() {
        clearTimeout(this.reloadTimer);
    }
});