package org.etecsagui.mvc.model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.etecsadao.DB;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author Rigoberto Leander Salgado Reyes <rlsalgado2006@gmail.com>
 * <p>
 * Copyright 2016 by Rigoberto Leander Salgado Reyes.
 * <p>
 * This program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http:www.gnu.org/licenses/agpl-3.0.txt) for more details.
 */
public class SearchService extends Service<List<List<Map<String, String>>>> {
    DB db;
    String partiaQuery = "";

    public void setDb(DB db) {
        this.db = db;
    }

    public void setPartiaQuery(String partiaQuery) {
        this.partiaQuery = partiaQuery;
    }

    @Override
    protected Task<List<List<Map<String, String>>>> createTask() {

        return new Task<List<List<Map<String, String>>>>() {
            @Override
            protected List<List<Map<String, String>>> call() throws Exception {
                return (db == null ? Collections.emptyList() : db.execute(partiaQuery));
            }
        };
    }
}
