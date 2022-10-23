package org.ergosapiens.explorer

import org.ergoplatform.ErgoBox
import org.ergoplatform.appkit.impl.ScalaBridge
import org.ergoplatform.explorer.client.model.OutputInfo
import org.ergoplatform.explorer.client.{DefaultApi, ExplorerApiClient}
import org.ergoplatform.restapi.client.{Asset, ErgoTransactionOutput, Registers}

import java.util
import scala.collection.JavaConversions._

class ErgoBoxExp {

  def getExplorerApi(apiUrl: String): DefaultApi = {
    new ExplorerApiClient(apiUrl).createService(classOf[DefaultApi])
  }

  def getBoxbyIDfromExplorer(api: DefaultApi, boxID: String): OutputInfo = {
    api.getApiV1BoxesP1(boxID).execute().body()
  }

  def getErgoBoxfromID(api: DefaultApi, boxID: String): ErgoBox = {
    val box: OutputInfo = getBoxbyIDfromExplorer(api, boxID)
    val tokens = new util.ArrayList[Asset](box.getAssets.size)
    for (asset <- box.getAssets) {
      tokens.add(new Asset().tokenId(asset.getTokenId).amount(asset.getAmount))
    }
    val registers = new Registers
    for (registerEntry <- box.getAdditionalRegisters.entrySet) {
      registers.put(registerEntry.getKey, registerEntry.getValue.serializedValue)
    }
    val boxConversion: ErgoTransactionOutput = new ErgoTransactionOutput()
      .ergoTree(box.getErgoTree)
      .boxId(box.getBoxId)
      .index(box.getIndex)
      .value(box.getValue)
      .transactionId(box.getTransactionId)
      .creationHeight(box.getCreationHeight)
      .assets(tokens)
      .additionalRegisters(registers)
    ScalaBridge.isoErgoTransactionOutput.to(boxConversion)
  }

}
