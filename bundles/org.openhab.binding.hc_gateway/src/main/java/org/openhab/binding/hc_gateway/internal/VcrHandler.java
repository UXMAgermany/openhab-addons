/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.hc_gateway.internal;

import com.bshg.homeconnect.hcpservice.*;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.openhab.binding.hc_gateway.internal.BindingConstants.OPERATION_STATE;
import static org.openhab.binding.hc_gateway.internal.BindingConstants.POWER_STATE;

/**
 * The {@link VcrHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author UXMA - Initial contribution
 */
@NonNullByDefault
public class VcrHandler extends BaseThingHandler {

    private final Logger logger = LoggerFactory.getLogger(VcrHandler.class);
    private @Nullable HomeAppliance homeAppliance;
    private @Nullable Configuration config;
    private final ThreadPoolExecutor threadPoolExecutor =
            new ThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

    public VcrHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (POWER_STATE.equals(channelUID.getId())) {
            if (command instanceof OnOffType) {
                logger.warn("handle Command: {}", command);
                switch (((OnOffType) command)) {
                    case ON:
                        homeAppliance.changeProperty("BSH.Common.Setting.PowerState","BSH.Common.EnumType.PowerState.On");
                        updateState(OPERATION_STATE, new StringType("ON"));
                        break;
                    case OFF:
                    default:
                        homeAppliance.changeProperty("BSH.Common.Setting.PowerState","BSH.Common.EnumType.PowerState.Standby");
                        updateState(OPERATION_STATE, new StringType("OFF"));
                        break;
                }
            }

            // TODO: handle command

            // Note: if communication with thing fails for some reason,
            // indicate that by setting the status with detail information:
            // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.COMMUNICATION_ERROR,
            // "Could not control device at IP address x.x.x.x");
        }
    }

    @Override
    public void initialize() {
        // logger.debug("Start initializing!");
        config = getConfigAs(Configuration.class);

        final List<String> requiredKeys = new ArrayList<>();
        final List<String> maximallyAllowedOptionKeys = new ArrayList<>();

        final HomeApplianceContext homeApplianceContext =
                new HomeApplianceContext(config.identifier, config.FMF, config.DDF, HomeApplianceGroup.CLEANING_ROBOT, Scenario.REMOTE_OPERATION,
                        requiredKeys, maximallyAllowedOptionKeys);
        final EndDeviceContext endDeviceContext =
                new EndDeviceContext("VCR", "DeviceID", "AppVersionName");
        URI uri = null;
        try {
            uri = new URI(config.networkAddress);
        } catch (URISyntaxException e) {
            logger.error("error:", e);
        }
        ConnectionContext connectionContext = new ConnectionContext(uri, config.aesKey, config.aesIV, false);

        final HomeApplianceConfiguration homeApplianceConfiguration =
                new HomeApplianceConfiguration(endDeviceContext, homeApplianceContext,
                        connectionContext);

        homeAppliance = HomeApplianceFactory.createHomeAppliance(threadPoolExecutor, ThreadPoolRunnable::new,
                homeApplianceConfiguration);

        homeAppliance.serviceState().observe()
                .first(serviceState -> ServiceState.INITIALIZED == serviceState)
                .subscribe(serviceState -> homeAppliance.connect());

        // TODO: Initialize the handler.
        // The framework requires you to return from this method quickly. Also, before leaving this method a thing
        // status from one of ONLINE, OFFLINE or UNKNOWN must be set. This might already be the real thing status in
        // case you can decide it directly.
        // In case you can not decide the thing status directly (e.g. for long running connection handshake using WAN
        // access or similar) you should set status UNKNOWN here and then decide the real status asynchronously in the
        // background.

        // set the thing status to UNKNOWN temporarily and let the background task decide for the real status.
        // the framework is then able to reuse the resources from the thing handler initialization.
        // we set this upfront to reliably check status updates in unit tests.
        updateStatus(ThingStatus.UNKNOWN);

        scheduler.execute(() -> {
           homeAppliance.connectionState().observe().subscribe(connectionState -> {
                switch (connectionState) {
                    case CONNECTED:
                        updateStatus(ThingStatus.ONLINE);
                        break;
                    case DISCONNECTED:
                    default:
                        updateStatus(ThingStatus.OFFLINE);
                }
            });
        });

        // logger.debug("Finished initializing!");

        // Note: When initialization can NOT be done set the status with more details for further
        // analysis. See also class ThingStatusDetail for all available status details.
        // Add a description to give user information to understand why thing does not work as expected. E.g.
        // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
        // "Can not access device as username and/or password are invalid");
    }
}