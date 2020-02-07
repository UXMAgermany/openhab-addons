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

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link BindingConstants} class defines common constants, which are
 * used across the whole binding.
 *
 * @author UXMA - Initial contribution
 */
@NonNullByDefault
public class BindingConstants {

    private static final String BINDING_ID = "hc_gateway";

    // All Thing Type UIDs
    public static final ThingTypeUID THING_VCR = new ThingTypeUID(BINDING_ID, "hc_vcr");

    // All Channel ids
    public static final String POWER_STATE = "powerstate";
    public static final String OPERATION_STATE = "operationstate";
}
