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

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.PrimitiveType;
import org.eclipse.smarthome.core.types.State;

/**
 * The {@link OperationStateType} represents the operations state of a home appliance
 *
 * @author UXMA - Initial contribution
 */
public enum OperationStateType implements PrimitiveType, State, Command {
    ON,
    OFF,
    RUNNING;

    private OperationStateType() {
    }

    @Override
    public String format(String pattern) {
        return String.format(pattern, this.toString());
    }

    @Override
    public String toString() {
        return this.toFullString();
    }

    @Override
    public String toFullString() {
        return super.toString();
    }

    @Override
    public <T extends State> @Nullable T as(@Nullable Class<T> target) {
        return null;
    }
}
