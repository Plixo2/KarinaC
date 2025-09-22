package org.karina.lang.lsp.impl;

import jdk.jfr.Event;
import karina.lang.Option;
import karina.lang.string.Escape;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.karina.lang.compiler.model_api.Model;
import org.karina.lang.compiler.stages.parser.gen.KarinaParser;
import org.karina.lang.compiler.utils.logging.DiagnosticCollection;
import org.karina.lang.lsp.lib.VirtualFile;
import org.karina.lang.lsp.lib.events.EventService;

import java.net.URI;

@Getter
public class DefaultVirtualFile implements VirtualFile {

    @Accessors(fluent = true)
    private final URI uri;

    @Accessors(fluent = true)
    private String content;
    private IntList lineCache;

    @Accessors(fluent = true)
    private int version;

    private boolean isOpen;


    public DefaultVirtualFile(URI uri, String content, int version, boolean isOpen) {
        this.uri = uri;
        this.setContent(content);
        this.version = version;
        this.isOpen = isOpen;
    }

    @Override
    @Contract(mutates = "this")
    public void updateContent(String newContent, int version) {
        this.setContent(newContent);
        this.version = version;
    }

    @Override
    public void updateContent(Range range, String newContent, int version, EventService eventService) {

        var startOffset = toIndex(this.lineCache, range.getStart());
        var endOffset = toIndex(this.lineCache, range.getEnd());

        if (startOffset < 0 ||
                endOffset < 0 ||
                startOffset > this.content.length() ||
                endOffset > this.content.length() ||
                startOffset > endOffset
        ) {
            eventService.warningMessage(
                     "Invalid offsets for updateContent: "
                            + startOffset
                            + " - "
                            + endOffset
                            + " in content of length "
                            + this.content.length()
            );
            System.err.println(
                            "Invalid offsets for updateContent: "
                            + startOffset
                            + " - "
                            + endOffset
                            + " in content of length "
                            + this.content.length()
            );
            System.err.println(this.content);
            System.err.println("Content:");
            System.err.println(Escape.escapeJava(this.content));
            System.err.println("Changes:");
            System.err.println(Escape.escapeJava(newContent));
            System.err.println("Change range: " + range);
            return;
        }

        var newString =
                this.content.substring(0, startOffset)
                        + newContent +
                this.content.substring(endOffset);
        this.setContent(newString);
        this.version = version;
    }

    @Override
    @Contract(mutates = "this")
    public void open() { this.isOpen = true; }


    @Override
    @Contract(mutates = "this")
    public void close() { this.isOpen = false; }



    @Override
    public String toString() {
        return "VirtualFile{" + "uri=" + this.uri + '}';
    }

    private void setContent(String content) {
        this.content = content;
        this.lineCache = buildLineCache(content);
    }

    private static IntList buildLineCache(String content) {
        IntList lineCache = new IntList();

        int index = 0;
        lineCache.add(0);
        while (index < content.length()) {
            char c = content.charAt(index);
            if (c == '\n') {
                lineCache.add(index + 1);
            }
            index++;
        }
        if (content.isEmpty() || content.charAt(content.length() - 1) != '\n') {
            lineCache.add(content.length());
        }


        return lineCache;
    }

    private static int toIndex(IntList lineCache, Position position) {
        var indexOfStartLine = lineStart(lineCache, position.getLine());
        if (indexOfStartLine == -1) {
            return -1;
        }
        return indexOfStartLine + position.getCharacter();
    }

    private static int lineStart(IntList lineCache, int line) {
        if (line < 0) {
            return -1;
        } else if (line >= lineCache.size()) {
            return -1;
        }
        return lineCache.get(line);
    }


}
