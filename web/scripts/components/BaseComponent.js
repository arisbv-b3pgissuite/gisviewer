/* Helper function to create a Component */
B3PGissuite.createComponent = function(className, options) {
    if(typeof B3PGissuite.component[className] === 'undefined') {
        throw('The class ' + className + ' is not defined. Maybe you forgot to include the source file?');
    }
    if(typeof B3PGissuite.idregistry[className] === 'undefined') {
        B3PGissuite.idregistry[className] = 0;
    }
    var nextid = B3PGissuite.idregistry[className]++;
    // Component id is lowercased className + incremental number
    var instanceId = (className.charAt(0).toLowerCase() + className.slice(1)) + nextid;
    B3PGissuite.instances[instanceId] = new B3PGissuite.component[className](options);
    return B3PGissuite.instances[instanceId];
};
/* Helper function to define a Component */
B3PGissuite.defineComponent = function(className, classDefinition) {
    // Constructor function of the classDefinition is the constructor for the new component
    B3PGissuite.component[className] = classDefinition.constructor;
    // Set prototype of new component to classDefinition (all functions and options in definition are accessible)
    B3PGissuite.component[className].prototype = classDefinition;
    // If the extend option is set, extend other component
    if(classDefinition.extend) {
        // Set the prototype of the component to the parents prototype, so all non-overridden functions from the parent are accessible
        B3PGissuite.component[className].prototype = jQuery.extend(B3PGissuite.createComponent(classDefinition.extend), B3PGissuite.component[className].prototype);
        // Set the contructor the the new component
        B3PGissuite.component[className].prototype.constructor = B3PGissuite.component[className];
        // Add the callParent function to be able to set the options on the parent object
        B3PGissuite.component[className].prototype.callParent = function(options) {
            // Extend the options with some defaultOptions (if present)
            options = jQuery.extend(classDefinition.defaultOptions || {}, options);
            // Call the parent constructor with the options
            B3PGissuite.component[classDefinition.extend].call(this, options);
        };
    }
    // Add event handling
    // Fire events
    B3PGissuite.component[className].prototype.fireEvent = function(evtName, evtData) {
        // Check if there are listeners registered
        if(B3PGissuite.events.hasOwnProperty(className) && B3PGissuite.events[className].hasOwnProperty(evtName)) {
            // Loop over all registered listeners
            for(var k in B3PGissuite.events[className][evtName]) {
                // Listener to temp var
                var registeredListener = B3PGissuite.events[className][evtName][k];
                // Call the registered listener handler with scope and eventData
                registeredListener.handler.call(registeredListener.scope, evtData);
            }
        }
    };
    // Register event listeners
    B3PGissuite.component[className].prototype.addListener = function(clsName, evtName, handler, scope) {
        // Check if there are listeners registered for targetClass
        if(!B3PGissuite.events.hasOwnProperty(clsName)) {
            B3PGissuite.events[clsName] = {};
        }
        // Check if there are listeners registered for event
        if(!B3PGissuite.events[clsName].hasOwnProperty(evtName)) {
            B3PGissuite.events[clsName][evtName] = [];
        }
        // Add listener for classname + eventname
        B3PGissuite.events[clsName][evtName].push({handler: handler, scope: scope || this});
    };
};
/* Helper function to get access to a component (for example var tree = B3PGissuite.get('TreeComponent'); ) */
B3PGissuite.get = function(className, id) {
    // Default instanceid is zero (the first instance)
    var idnumber = id || 0;
    var instanceId = (className.charAt(0).toLowerCase() + className.slice(1)) + idnumber;
    if(typeof B3PGissuite.instances[instanceId] === 'undefined') {
        return null;
    }
    return B3PGissuite.instances[instanceId];
};

B3PGissuite.whichTransitionEvent = function() {
    var t;
    var el = document.createElement('fakeelement');
    var transitions = {
        'transition': 'transitionend',
        'OTransition': 'oTransitionEnd',
        'MozTransition': 'transitionend',
        'WebkitTransition': 'webkitTransitionEnd'
    }

    for (t in transitions) {
        if (el.style[t] !== undefined) {
            return transitions[t];
        }
    }
    return null;
};

B3PGissuite.attachTransitionListener = function(obj, handler) {
    var transitionEnd = B3PGissuite.whichTransitionEvent(),
            me = this;
    if (transitionEnd === null)
        return;
    obj.addEventListener(transitionEnd,handler, false);
};      

/* BaseComponent */
B3PGissuite.defineComponent('BaseComponent', {
    defaultOptions: {
        id: '',
        tabid: '', 
        title: ''
    },
    constructor: function BaseComponent(options) {
        this.options = jQuery.extend(this.defaultOptions, options);
        this.component = null;
    },
    init: function() {
        throw('Init must be implemented in a sub-class');
    },
    render: function(domId) {
        this.tabPanel = jQuery('#' + domId);
        this.tabPanel.append(this.component);
        this.afterRender();
    },
    renderTab: function(tabComponent) {
        this.tabComponent = tabComponent;
        var domId = this.tabComponent.createTab(this.options.tabid, this.options.title, this.options.taboptions || {});
        this.render(domId);
    },
    afterRender: function() {},
    getTabComponent: function() {
        if(this.tabComponent) return this.tabComponent;
        return null;
    },
    getTabPanel: function() {
        return this.tabPanel;
    }
});