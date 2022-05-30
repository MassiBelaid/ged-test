/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pom.sample;

import fr.atos.alfresco.AlfrescoClientPublic;
import fr.atos.alfresco.EndPoint;
import fr.atos.alfresco.http.ClientHTTP;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Integration Test (IT) for Hello World web script.
 *
 * @author martin.bergljung@alfresco.com
 * @version 1.0
 * @since 3.0
 */
public class HelloWorldWebScriptIT {

    private AlfrescoClientPublic clientPublic;
    private ClientHTTP client;

    @Before
    public void setUp() {
        client = new ClientHTTP();
        clientPublic = new AlfrescoClientPublic(client);
    }

    @Test
    public void testWebScriptCall() throws Exception {
        String expectedResponse = "Message: 'Hello from JS!' 'HelloFromJava'";
        String response = client.get(EndPoint.get() + "/s/sample/helloworld", String.class);

        assertThat(response, is(expectedResponse));

    }

}