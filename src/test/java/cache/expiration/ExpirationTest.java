/*
 * Copyright 2016 BananaRama.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cache.expiration;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.bananarama.BananaRama;
import org.bananarama.cache.IndexedCollectionAdapter;
import org.bananarama.cache.annotation.BufferedOnIndexedCollection;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Guglielmo De Concini
 */
public class ExpirationTest {
    
    public ExpirationTest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testExpiration() throws InterruptedException {
        List<TimedEntry> entries = IntStream.range(0, 5)
                .mapToObj(TimedEntry::new )
                .collect(Collectors.toList());
        
        IndexedCollectionAdapter adap = new IndexedCollectionAdapter(new BananaRama());
        
        adap.create(TimedEntry.class)
                .from(entries.stream());
        
        Assert.assertEquals(entries.size(),adap.read(TimedEntry.class).all().count());
        
        //Wait for more than the TTL time
        BufferedOnIndexedCollection anno = TimedEntry.class.getAnnotation(BufferedOnIndexedCollection.class);
        long wait = Long.valueOf(ExpiringCollectionProvider.time);
        
        Assert.assertTrue(wait > 0);
        
        //Wait for the entry to be expired
        Thread.sleep((wait+1)*1000);
        
        //Cache should be empty now
        Assert.assertEquals(0,adap.read(TimedEntry.class).all().count());
        
    }
}
