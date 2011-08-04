(function($) {
	$.fn.setStatus = function(options) {
		var defaults = {
			status: true,
			message: '',
			available: {
				color: '#fff',
				background: '#6f8f52'
			},
			unavailable: {
				color: '#fff',
				background: '#ad2930'
			},
			common: {
				'font': 'arial,verdana,tahoma,sans-serif',
				'font-size': '0.8em',
				'font-weight': 'bold',
				'padding': '3px 5px',
				'border-radius': '4px',
				'display': 'block'
			}
		};

		options = $.extend(defaults, options);

		return this.each(function() {
			var statusNode = $('td:eq(2)', this);

			if (options.status) {
				statusNode.text('✔ ' + options.message);
				statusNode.css($.extend(options.common, options.available));
			} else {
				statusNode.text('✗ ' + options.message);
				statusNode.css($.extend(options.common, options.unavailable));
			}
		});
	};

	$.fn.hideStatus = function() {
		return this.each(function() {
			var statusNode = $('td:eq(2)', this);
			statusNode.text('');
			statusNode.css({ display: 'none' });
		});
	};
})(jQuery);
