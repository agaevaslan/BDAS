package org.example.`1lab`

import org.w3c.dom.Document
import org.w3c.dom.Node
import org.xml.sax.InputSource
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class Obfuscator(private val inPath: String, private val outPath: String, private val mode: String) {
    private val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    private val target = "Q5A8ZWS0XEDC6RFVT9GBY4HNU3J2MI1KO7LPabcdefghijklmnopqrstuvwxyz"

    fun process() {
        val document = readXml(inPath)
        transformDoc(document, mode)
        val outputStream = FileOutputStream(outPath)
        writeXml(document, outputStream)
    }

    private fun readXml(path: String): Document {
        val xmlFile = File(path)

        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlInput = InputSource(StringReader(xmlFile.readText()))
        return dBuilder.parse(xmlInput)
    }

    private fun writeXml(doc: Document?, outStream: OutputStream) {
        val tf = TransformerFactory.newInstance()
        val transformer = tf.newTransformer()
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")
        transformer.setOutputProperty(OutputKeys.METHOD, "xml")
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
        transformer.transform(DOMSource(doc), StreamResult(OutputStreamWriter(outStream, StandardCharsets.UTF_8)))
    }

    private fun transformDoc(document: Document, mode: String) {
        val transformFun = if (mode == "obf") ::obfuscate else ::unobfuscate

        val rootNode: Node = document.documentElement
        val nodesToTraverse = ArrayList<Node>()
        nodesToTraverse.add(rootNode)
        val iter = nodesToTraverse.listIterator()

        while (iter.hasNext()) {
            val node = iter.next()
            if (node.nodeType == Node.TEXT_NODE) {
                val nodeValue = node.nodeValue
                val res = transformFun(nodeValue)
                node.nodeValue = res
            }
            
            if (node.hasAttributes()) {
                val nodeAttributes = node.attributes
                for (i in 0 until nodeAttributes.length) {
                    val item = nodeAttributes.item(i)
                    iter.add(item)
                    iter.previous()
                }
            }
            
            if (node.hasChildNodes()) {
                val childNodes = node.childNodes
                for (i in 0 until childNodes.length) {
                    val item = childNodes.item(i)
                    iter.add(item)
                    iter.previous()
                }
            }
        }
    }

    private fun obfuscate(s: String): String? {
        val result = StringBuilder()
        for (element in s) {
            val index = source.indexOf(element)
            result.append(if (index > -1) target[index] else element)
        }
        return result.toString()
    }

    private fun unobfuscate(s: String): String? {
        val result = StringBuilder()
        for (element in s) {
            val index = target.indexOf(element)
            result.append(if (index > -1) source[index] else element)
        }
        return String(result)
    }
}


