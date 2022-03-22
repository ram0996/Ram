_affirm_config = {
    public_api_key:  ACC.addons.affirmpaymentaddon.affirm_public_key,
    script:          ACC.addons.affirmpaymentaddon.affirm_site_url,
    session_id:      ACC.addons.affirmpaymentaddon.affirm_session_id
    };
(function(l,g,m,e,a,f,b){var d,c=l[m]||{},h=document.createElement(f),n=document.getElementsByTagName(f)[0],k=function(a,b,c){return function(){a[b]._.push([c,arguments])}};c[e]=k(c,e,"set");d=c[e];c[a]={};c[a]._=[];d._=[];c[a][b]=k(c,a,b);a=0;for(b="set add save post open empty reset on off trigger ready setProduct".split(" ");a<b.length;a++)d[b[a]]=k(c,e,b[a]);a=0;for(b=["get","token","url","items"];a<b.length;a++)d[b[a]]=function(){};h.async=!0;h.src=g[f];n.parentNode.insertBefore(h,n);delete g[f];d(g);l[m]=c})(window,_affirm_config,"affirm","checkout","ui","script","ready");

ACC.affirm = {

   _autoload: [
        ['initAffirmPayment', $(".affirm-checkout").length != 0],
        ['initAffirmTracking', $("#affirmOrderTracking").length != 0]
   ],

   vcnSuccessCallbackFunction: function (e) {
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/multi/payment-method/affirm-vcn',
            async: false,
            type: 'GET',
            data: {"timestamp" : Date.now()},
            success: function (result) {

                $('#js-affirm-form').html(result);
                $('#card_nameOnCard').val(e.cardholder_name);
                $('#card_accountNumber').val(e.number);
                $('#card_cvNumber').val(e.cvv);
                $('#affirm_id').val(e.id);

                var month =  parseInt(e.expiration.substring(0, 2));
                var year =  parseInt("20" + e.expiration.substring(2, 4));
                $('#card_expirationMonth').val(month);
                $('#card_expirationYear').val(year);

                //$('#billTo_city').val(e.billing_address.city);
                //$('#billTo_state').val(e.billing_address.state);
                //$('#billTo_postalCode').val(e.billing_address.zipcode);
                //$('#billTo_street1').val(e.billing_address.line1);
                //if(e.billing_address.line2 != undefined){
                //   $('#billTo_street2').val(e.billing_address.line2);
                //}

                $('#silentOrderPostForm').submit();
            }
        });



   },

   vcnErrorCallbackFunction: function () {
        alert("Sorry, unexpectedd error happened. Please choose another payment method.");
   },

   initAffirmTracking: function () {
        $.ajax({
            url: ACC.config.encodedContextPath + '/checkout/orderConfirmation/tracking/' + $("#affirmOrderTracking").data('code'),
            async: false,
            type: 'GET',
            success: function (result) {
                affirm.ui.ready(function(){
                    affirm.analytics.trackOrderConfirmed(result.order, result.products);
                });
            }
        });
   },

   initAffirmPayment: function () {

       $('.js-affirm-pay-button').on('click', function() {
           $.ajax({
                url: ACC.config.encodedContextPath + '/checkout/affirm/affirmCheckout',
                async: false,
                type: 'POST',
                data: {"CSRFToken" : ACC.config.CSRFToken, "vcnmode":$(this).data('vcnmode')},
                success: function (result) {
                    if($('.js-affirm-pay-button').data('vcnmode') === true){
                        var jsonObj = {
                              success: function(card_details) {
                                 ACC.affirm.vcnSuccessCallbackFunction(card_details);
                              },
                              error: function(error_response) {
                                 ACC.affirm.vcnErrorCallbackFunction();
                              },
                              checkout_data: result
                        };
                        //affirm.checkout(jsonObj);
                        affirm.checkout.open_vcn(jsonObj);
                    }else{
                        affirm.checkout(result);
                        affirm.checkout.open();
                    }
                }
           });
       });

       $('.js-payment-mode-selector').on('change', function() {
            var showClass = $(this).data('show');
            $(showClass).removeClass('hidden');
            var hideClass = $(this).data('hide');
            $(hideClass).addClass('hidden');
            $('#silentOrderPostForm').attr('action', $(this).data('url'));

       });

       $('.js-payment-mode-selector:first').click();
       ACC.affirm.bindUseDeliveryAddress();
       ACC.affirm.bindSubmitSilentOrderPostForm();
       ACC.affirm.bindCreditCardAddressForm();

       // check the checkbox
       $("#affirmUseDeliveryAddress").click();
   },

   bindUseDeliveryAddress: function ()
   	{
   		$('#affirmUseDeliveryAddress').on('change', function ()
   		{
   			if ($('#affirmUseDeliveryAddress').is(":checked"))
   			{
   				var options = {'countryIsoCode': $('#affirmUseDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
   				ACC.affirm.enableAddressForm();
   				ACC.affirm.displayCreditCardAddressForm(options, ACC.affirm.useDeliveryAddressSelected);
   				ACC.affirm.disableAddressForm();
   			}
   			else
   			{
   				ACC.affirm.clearAddressForm();
   				ACC.affirm.enableAddressForm();
   			}
   		});

   		if ($('#affirmUseDeliveryAddress').is(":checked"))
   		{
   			var options = {'countryIsoCode': $('#affirmUseDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
   			ACC.affirm.enableAddressForm();
   			ACC.affirm.displayCreditCardAddressForm(options, ACC.affirm.useDeliveryAddressSelected);
   			ACC.affirm.disableAddressForm();
   		}
   	},

   	bindSubmitSilentOrderPostForm: function ()
   	{
   		$('.js-submit_affirmForm').click(function ()
   		{
   			ACC.common.blockFormAndShowProcessingMessage($(this));
   			$('.billingAddressForm').filter(":hidden").remove();
   			ACC.affirm.enableAddressForm();
   			$('#affirmAddressForm').submit();
   		});
   	},

   	isEmpty: function (obj)
   	{
   		if (typeof obj == 'undefined' || obj === null || obj === '') return true;
   		return false;
   	},

   	disableAddressForm: function ()
   	{
   		$('input[id^="address\\."]', '#affirmAddressForm').prop('disabled', true);
   		$('select[id^="address\\."]', '#affirmAddressForm').prop('disabled', true);
   	},

   	enableAddressForm: function ()
   	{
   		$('input[id^="address\\."]', '#affirmAddressForm').prop('disabled', false);
   		$('select[id^="address\\."]', '#affirmAddressForm').prop('disabled', false);
   	},

   	clearAddressForm: function ()
   	{
   		$('input[id^="address\\."]', '#affirmAddressForm').val("");
   		$('select[id^="address\\."]', '#affirmAddressForm').val("");
   	},

   	useDeliveryAddressSelected: function ()
   	{
   		if ($('#affirmUseDeliveryAddress').is(":checked"))
   		{
   			$('#address\\.country', '#affirmAddressForm').val($('#affirmUseDeliveryAddressData').data('countryisocode'));
   			ACC.affirm.disableAddressForm();
   		}
   		else
   		{
   			ACC.affirm.clearAddressForm();
   			ACC.affirm.enableAddressForm();
   		}
   	},



   	bindCreditCardAddressForm: function ()
   	{
   		$('#affirmBillingCountrySelector :input').on("change", function ()
   		{
   			var countrySelection = $(this).val();
   			var options = {
   				'countryIsoCode': countrySelection,
   				'useDeliveryAddress': false
   			};
   			ACC.affirm.displayCreditCardAddressForm(options);
   		})
   	},

   	displayCreditCardAddressForm: function (options, callback)
   	{
   		$.ajax({
   			url: ACC.config.encodedContextPath + '/checkout/multi/sop/billingaddressform',
   			async: true,
   			data: options,
   			dataType: "html",
   			beforeSend: function ()
   			{
   				$('#affirmBillingAddressForm').html(ACC.affirm.spinner);
   			}
   		}).done(function (data){
            $("#affirmBillingAddressForm").html(data);
            if (typeof callback == 'function')
            {
                callback.call();
            }
        });
   	}

}
